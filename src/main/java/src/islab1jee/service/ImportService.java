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
import java.util.*;
import java.util.logging.Logger;

@ApplicationScoped
public class ImportService {

    @Inject
    private ImportRepository importRepository;

    @PersistenceContext
    private EntityManager em;

    private static final Logger logger = Logger.getLogger(ImportService.class.getName());

    @Transactional
    public ImportOperation processImport(InputStream inputStream) {
        ImportOperation op = new ImportOperation();
        op.setTimestamp(LocalDateTime.now());

        try {
            String jsonText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("\uFEFF", "")
                    .trim();

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
            logger.severe("Ошибка при импорте: " + e.getMessage());
            op.setStatus(ImportStatus.FAILED);
            op.setAddedCount(0);
            op.setErrorMessage("Ошибка импорта: " + e.getMessage());
        }

        importRepository.save(op);
        return op;
    }

    private boolean persistMovie(JsonNode json) {
        try {
            Coordinates coords = buildCoordinates(json.path("coordinates"));
            validateNotNull(coords, "Movie.coordinates");
            em.persist(coords);

            Person director = buildPerson(json.path("director"));
            validatePersonOrThrow(director, "Movie.director");

            Person operator = buildPerson(json.path("operator"));
            validatePersonOrThrow(operator, "Movie.operator");

            Person screenwriter = json.has("screenwriter") && !json.get("screenwriter").isNull()
                    ? buildPerson(json.get("screenwriter"))
                    : null;
            if (screenwriter != null) validatePersonOrThrow(screenwriter, "Movie.screenwriter");

            em.persist(director);
            em.persist(operator);
            if (screenwriter != null) em.persist(screenwriter);

            Movie movie = new Movie();
            movie.setName(json.path("name").asText(null));
            movie.setCoordinates(coords);
            movie.setCreationDate(LocalDate.now());
            movie.setOscarsCount(json.path("oscarsCount").asLong(0));
            if (json.has("budget") && !json.get("budget").isNull())
                movie.setBudget(json.path("budget").asLong());
            movie.setTotalBoxOffice(json.path("totalBoxOffice").asLong(0));
            movie.setMpaaRating(parseEnum(json, "mpaaRating", MpaaRating.class, true));
            movie.setDirector(director);
            movie.setOperator(operator);
            movie.setScreenwriter(screenwriter);
            movie.setLength(json.path("length").asLong(0));
            movie.setGoldenPalmCount(json.path("goldenPalmCount").asInt(0));
            movie.setUsaBoxOffice(json.path("usaBoxOffice").asDouble(0));
            movie.setTagline(json.path("tagline").asText(null));
            movie.setGenre(parseEnum(json, "genre", MovieGenre.class, true));

            validateNotNull(movie.getName(), "Movie.name");
            em.persist(movie);

            return true;
        } catch (Exception e) {
            logger.severe("Ошибка при создании Movie: " + e.getMessage());
            throw new RuntimeException("Ошибка создания Movie: " + e.getMessage(), e);
        }
    }

    private boolean persistCoordinates(JsonNode json) {
        try {
            if (json.has("objects") && json.get("objects").isArray()) {
                for (JsonNode obj : json.get("objects"))
                    em.persist(buildCoordinates(obj));
            } else {
                em.persist(buildCoordinates(json));
            }
            return true;
        } catch (Exception e) {
            logger.severe("Ошибка при создании Coordinates: " + e.getMessage());
            throw new RuntimeException("Ошибка создания Coordinates", e);
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
                for (JsonNode obj : json.get("objects"))
                    em.persist(buildLocation(obj));
            } else {
                em.persist(buildLocation(json));
            }
            return true;
        } catch (Exception e) {
            logger.severe("Ошибка при создании Location: " + e.getMessage());
            throw new RuntimeException("Ошибка создания Location", e);
        }
    }

    private Location buildLocation(JsonNode json) {
        Location loc = new Location();
        loc.setX(json.path("x").floatValue());
        loc.setY(json.path("y").doubleValue());
        loc.setZ(json.path("z").longValue());
        loc.setName(json.path("name").asText(null));

        validateNotNull(loc.getName(), "Location.name");
        return loc;
    }

    private boolean persistPerson(JsonNode json) {
        try {
            Person person = buildPerson(json);
            validatePersonOrThrow(person, "Person");
            em.persist(person);
            return true;
        } catch (Exception e) {
            logger.severe("Ошибка при создании Person: " + e.getMessage());
            throw new RuntimeException("Ошибка создания Person", e);
        }
    }

    private Person buildPerson(JsonNode json) {
        if (json == null || json.isNull() || json.isMissingNode())
            throw new IllegalArgumentException("Person node is null or missing");

        Location loc = null;
        if (json.has("location") && !json.get("location").isNull()) {
            JsonNode locNode = json.get("location");
            if (locNode.has("objects") && locNode.get("objects").isArray() && locNode.get("objects").size() > 0)
                loc = buildLocation(locNode.get("objects").get(0));
            else
                loc = buildLocation(locNode);

            validateNotNull(loc, "Person.location");
            em.persist(loc);
        }

        Person person = new Person();
        person.setName(json.path("name").asText(null));
        person.setHairColor(parseEnum(json, "hairColor", Color.class, true));
        person.setEyeColor(parseEnum(json, "eyeColor", Color.class, false));
        person.setLocation(loc);
        person.setWeight(json.path("weight").asDouble(0));
        person.setPassportID(json.path("passportID").asText(null));
        person.setNationality(parseEnum(json, "nationality", Country.class, true));

        return person;
    }

    private void validatePersonOrThrow(Person person, String context) {
        if (person == null)
            throw new IllegalArgumentException(context + " = null");

        List<String> missing = new ArrayList<>();
        if (person.getName() == null || person.getName().isBlank()) missing.add("name");
        if (person.getHairColor() == null) missing.add("hairColor");
        if (person.getPassportID() == null || person.getPassportID().isBlank()) missing.add("passportID");
        if (person.getLocation() == null) missing.add("location");

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(context + " отсутствуют обязательные поля: " + missing);
        }
    }

    private <E extends Enum<E>> E parseEnum(JsonNode json, String field, Class<E> enumClass, boolean required) {
        if (!json.has(field) || json.get(field).isNull()) {
            if (required)
                throw new IllegalArgumentException("Поле '" + field + "' отсутствует или null");
            else return null;
        }
        String text = json.path(field).asText();
        try {
            return Enum.valueOf(enumClass, text);
        } catch (Exception e) {
            throw new IllegalArgumentException("Недопустимое значение '" + text + "' для поля " + field
                    + " (ожидается одно из: " + Arrays.toString(enumClass.getEnumConstants()) + ")");
        }
    }

    private void validateNotNull(Object obj, String field) {
        if (obj == null)
            throw new IllegalArgumentException("Поле " + field + " не может быть null");
    }
}
