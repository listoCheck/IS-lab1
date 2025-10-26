package src.islab1jee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import src.islab1jee.enums.ImportStatus;
import src.islab1jee.enums.MovieGenre;
import src.islab1jee.enums.MpaaRating;
import src.islab1jee.enums.Color;
import src.islab1jee.enums.Country;
import src.islab1jee.model.coordinates.Coordinates;
import src.islab1jee.model.importobjects.ImportOperation;
import src.islab1jee.model.location.Location;
import src.islab1jee.model.movie.Movie;
import src.islab1jee.model.person.Person;
import src.islab1jee.repository.ImportRepository;
import src.islab1jee.utils.JsonUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ImportService {

    @Inject
    private ImportRepository importRepository;

    @Inject
    private JsonUtils jsonUtils;

    @PersistenceContext
    private EntityManager em;



    @Transactional
    public ImportOperation processImport(InputStream inputStream) {
        Logger logger = Logger.getLogger(ImportService.class.getName());
        ImportOperation op = new ImportOperation();

        try {
            logger.info("Начало импорта объектов.");

            String jsonText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();
            jsonText = jsonText.replace("\uFEFF", "");

            StringBuilder sb = new StringBuilder();
            boolean insideJson = false;
            for (String line : jsonText.split("\\R")) {
                line = line.trim();
                if (line.startsWith("{") || line.startsWith("[")) insideJson = true;
                if (insideJson) sb.append(line).append("\n");
            }
            jsonText = sb.toString().trim();

            logger.info("Файл прочитан. Длина текста: " + jsonText.length() + " символов.");


            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonText);

            List<JsonNode> objects = new ArrayList<>();

            if (root.has("type") && root.has("objects") && root.get("objects").isArray()) {
                logger.info("Формат: {\"type\": ..., \"objects\": [...]}");
                for (JsonNode obj : root.get("objects")) objects.add(obj);
            } else if (root.isArray()) {
                logger.info("Формат: массив JSON объектов.");
                for (JsonNode obj : root) objects.add(obj);
            } else {
                logger.info("Формат: одиночный объект.");
                objects.add(root);
            }

            logger.info("Найдено объектов для импорта: " + objects.size());

            int savedCount = 0;
            String typeHint = root.path("type").asText("");

            for (int i = 0; i < objects.size(); i++) {
                JsonNode json = objects.get(i);
                String type = json.has("type") ? json.path("type").asText() : typeHint;
                logger.info("Обработка объекта #" + (i + 1) + ", тип: " + type);

                boolean persisted = switch (type) {
                    case "Movie" -> persistMovie(json);
                    case "Coordinates" -> persistCoordinates(json);
                    case "Location" -> persistLocation(json);
                    default -> json.has("coordinates") && json.has("director") ? persistMovie(json) : false;
                };

                if (persisted) {
                    savedCount++;
                    logger.info("Объект #" + (i + 1) + " успешно сохранен.");
                } else {
                    logger.warning("Не удалось сохранить объект #" + (i + 1) + ".");
                }
            }

            op.setStatus(ImportStatus.SUCCESS);
            op.setAddedCount(savedCount);
            logger.info("Импорт завершен. Успешно добавлено объектов: " + savedCount);

        } catch (Exception e) {
            logger.severe("Ошибка при импорте объектов: " + e.getMessage());
            e.printStackTrace();
            op.setStatus(ImportStatus.FAILED);
            op.setAddedCount(0);
            op.setErrorMessage(e.getMessage());
        }

        importRepository.save(op);
        logger.info("Операция импорта сохранена в базе.");
        return op;
    }




    // ---------- Movie ----------

    private boolean persistMovie(JsonNode json) {
        try {
            Coordinates coords = buildCoordinates(json.path("coordinates"));
            em.persist(coords);

            Person director = buildPerson(json.path("director"));
            em.persist(director);

            Person screenwriter = null;
            if (json.has("screenwriter") && !json.get("screenwriter").isNull()) {
                screenwriter = buildPerson(json.get("screenwriter"));
                em.persist(screenwriter);
            }

            Person operator = buildPerson(json.path("operator"));
            em.persist(operator);

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
            movie.setScreenwriter(screenwriter);
            movie.setOperator(operator);
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

    // ---------- Coordinates ----------

    private boolean persistCoordinates(JsonNode json) {
        try {
            if (json.has("objects") && json.get("objects").isArray()) {
                for (JsonNode obj : json.get("objects")) {
                    Coordinates coords = buildCoordinates(obj);
                    em.persist(coords);
                }
            } else {
                Coordinates coords = buildCoordinates(json);
                em.persist(coords);
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

    // ---------- Location ----------

    private boolean persistLocation(JsonNode json) {
        try {
            if (json.has("objects") && json.get("objects").isArray()) {
                for (JsonNode obj : json.get("objects")) {
                    Location loc = buildLocation(obj);
                    em.persist(loc);
                }
            } else {
                Location loc = buildLocation(json);
                em.persist(loc);
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

    // ---------- Person ----------

    private Person buildPerson(JsonNode json) {
        if (json.isNull() || json.isMissingNode()) return null;

        Location loc = null;
        if (json.has("location") && !json.get("location").isNull()) {
            loc = buildLocation(json.get("location"));
            em.persist(loc);
        }

        Person person = new Person();
        person.setName(json.path("name").asText());
        if (json.has("eyeColor") && !json.get("eyeColor").isNull())
            person.setEyeColor(Color.valueOf(json.path("eyeColor").asText()));
        person.setHairColor(Color.valueOf(json.path("hairColor").asText()));
        person.setLocation(loc);
        person.setWeight(json.path("weight").asDouble());
        person.setPassportID(json.path("passportID").asText());
        if (json.has("nationality") && !json.get("nationality").isNull())
            person.setNationality(Country.valueOf(json.path("nationality").asText()));

        return person;
    }
}
