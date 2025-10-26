package src.islab1jee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import src.islab1jee.enums.*;
import src.islab1jee.model.coordinates.Coordinates;
import src.islab1jee.model.importobjects.ImportOperation;
import src.islab1jee.model.location.Location;
import src.islab1jee.model.movie.Movie;
import src.islab1jee.model.person.Person;
import src.islab1jee.repository.ImportRepository;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class ImportService {

    @Inject
    private ImportRepository importRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public ImportOperation processImport(InputStream inputStream) {
        Logger logger = Logger.getLogger(ImportService.class.getName());
        ImportOperation op = new ImportOperation();
        op.setTimestamp(LocalDateTime.now());

        try {
            String jsonText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim()
                    .replace("\uFEFF", "");

            // Оставляем только JSON
            StringBuilder sb = new StringBuilder();
            boolean insideJson = false;
            for (String line : jsonText.split("\\R")) {
                line = line.trim();
                if (line.startsWith("{") || line.startsWith("[")) insideJson = true;
                if (insideJson) sb.append(line).append("\n");
            }
            jsonText = sb.toString().trim();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonText);

            List<JsonNode> objects = new ArrayList<>();
            if (root.has("type") && root.has("objects") && root.get("objects").isArray()) {
                for (JsonNode obj : root.get("objects")) objects.add(obj);
            } else if (root.isArray()) {
                for (JsonNode obj : root) objects.add(obj);
            } else {
                objects.add(root);
            }

            int savedCount = 0;
            String typeHint = root.path("type").asText("");

            for (JsonNode json : objects) {
                String type = json.has("type") ? json.path("type").asText() : typeHint;

                boolean persisted = switch (type) {
                    case "Movie" -> persistMovie(json);
                    case "Coordinates" -> persistCoordinates(json);
                    case "Location" -> persistLocation(json);
                    case "Person" -> persistPerson(json);
                    default -> json.has("coordinates") && json.has("director") ? persistMovie(json) : false;
                };

                if (persisted) savedCount++;
            }

            op.setStatus(ImportStatus.SUCCESS);
            op.setAddedCount(savedCount);

        } catch (Exception e) {
            e.printStackTrace();
            op.setStatus(ImportStatus.FAILED);
            op.setAddedCount(0);
            op.setErrorMessage(e.getMessage());
        }

        importRepository.save(op);
        return op;
    }

    private boolean persistMovie(JsonNode json) {
        try {
            Coordinates coords = buildCoordinates(json.path("coordinates"));
            em.persist(coords);

            Person director = buildPerson(json.path("director"));
            Person operator = buildPerson(json.path("operator"));
            Person screenwriter = json.has("screenwriter") && !json.get("screenwriter").isNull()
                    ? buildPerson(json.get("screenwriter"))
                    : null;

            if (!validatePerson(director) || !validatePerson(operator)) return false;

            em.persist(director);
            em.persist(operator);
            if (screenwriter != null && validatePerson(screenwriter)) em.persist(screenwriter);

            Movie movie = new Movie();
            movie.setName(json.path("name").asText());
            movie.setCoordinates(coords);
            movie.setCreationDate(LocalDate.now());
            movie.setOscarsCount(json.path("oscarsCount").asLong());
            if (json.has("budget") && !json.get("budget").isNull())
                movie.setBudget(json.path("budget").asLong());
            movie.setTotalBoxOffice(json.path("totalBoxOffice").asLong());
            movie.setMpaaRating(MpaaRating.valueOf(json.path("mpaaRating").asText("G")));
            movie.setDirector(director);
            movie.setOperator(operator);
            movie.setScreenwriter(screenwriter);
            movie.setLength(json.path("length").asLong());
            movie.setGoldenPalmCount(json.path("goldenPalmCount").asInt());
            movie.setUsaBoxOffice(json.path("usaBoxOffice").asDouble());
            movie.setTagline(json.path("tagline").asText());
            movie.setGenre(MovieGenre.valueOf(json.path("genre").asText()));

            em.persist(movie);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean persistCoordinates(JsonNode json) {
        try {
            if (json.has("objects") && json.get("objects").isArray()) {
                for (JsonNode obj : json.get("objects")) em.persist(buildCoordinates(obj));
            } else {
                em.persist(buildCoordinates(json));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Coordinates buildCoordinates(JsonNode json) {
        Coordinates coords = new Coordinates();
        coords.setX(json.path("x").floatValue());
        coords.setY(json.path("y").longValue());
        return coords;
    }

    private boolean persistLocation(JsonNode json) {
        try {
            if (json.has("objects") && json.get("objects").isArray()) {
                for (JsonNode obj : json.get("objects")) em.persist(buildLocation(obj));
            } else {
                em.persist(buildLocation(json));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Location buildLocation(JsonNode json) {
        Location loc = new Location();
        loc.setX(json.path("x").floatValue());
        loc.setY(json.path("y").doubleValue());
        loc.setZ(json.path("z").longValue());
        loc.setName(json.path("name").asText());
        return loc;
    }

    private boolean persistPerson(JsonNode json) {
        try {
            Person person = buildPerson(json);
            if (!validatePerson(person)) return false;
            em.persist(person);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Person buildPerson(JsonNode json) {
        Logger logger = Logger.getLogger(ImportService.class.getName());

        if (json == null || json.isNull() || json.isMissingNode()) {
            logger.warning("Person node is null or missing");
            return null;
        }

        Location loc = null;
        if (json.has("location") && !json.get("location").isNull()) {
            JsonNode locNode = json.get("location");
            logger.info("Found location node for person: " + locNode.toString());

            if (locNode.has("objects") && locNode.get("objects").isArray() && locNode.get("objects").size() > 0) {
                logger.info("Location node contains 'objects' array. Using the first object.");
                loc = buildLocation(locNode.get("objects").get(0));
            } else {
                logger.info("Location node is a single object.");
                loc = buildLocation(locNode);
            }

            logger.info("Persisting Location: " + loc.getName() + " (" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ")");
            em.persist(loc);
        } else {
            logger.info("No location provided for person.");
        }

        Person person = new Person();
        person.setName(json.has("name") ? json.path("name").asText(null) : null);
        person.setHairColor(json.has("hairColor") && !json.get("hairColor").isNull() ?
                Color.valueOf(json.path("hairColor").asText()) : null);
        person.setEyeColor(json.has("eyeColor") && !json.get("eyeColor").isNull() ?
                Color.valueOf(json.path("eyeColor").asText()) : null);
        person.setLocation(loc);
        person.setWeight(json.has("weight") ? json.path("weight").asDouble(0) : 0);
        person.setPassportID(json.has("passportID") ? json.path("passportID").asText(null) : null);
        person.setNationality(json.has("nationality") && !json.get("nationality").isNull() ?
                Country.valueOf(json.path("nationality").asText()) : null);

        logger.info("Built Person: " + person.getName() + ", hairColor=" + person.getHairColor()
                + ", eyeColor=" + person.getEyeColor() + ", passportID=" + person.getPassportID()
                + ", location=" + (loc != null ? loc.getName() : "null"));

        return person;
    }


    private boolean validatePerson(Person person) {
        if (person == null) return false;
        List<String> missing = new ArrayList<>();
        if (person.getName() == null || person.getName().isBlank()) missing.add("name");
        if (person.getHairColor() == null) missing.add("hairColor");
        if (person.getPassportID() == null || person.getPassportID().isBlank()) missing.add("passportID");
        if (person.getLocation() == null) missing.add("location");

        if (!missing.isEmpty()) {
            Logger.getLogger(ImportService.class.getName())
                    .warning("Person не сохранен. Отсутствуют обязательные поля: " + missing);
            return false;
        }
        return true;
    }
}
