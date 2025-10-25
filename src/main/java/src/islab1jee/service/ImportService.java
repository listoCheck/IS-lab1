package src.islab1jee.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.json.JsonObject;
import jakarta.json.JsonArray;
import src.islab1jee.enums.ImportStatus;
import src.islab1jee.model.importobjects.ImportOperation;
import src.islab1jee.repository.ImportRepository;
import src.islab1jee.utils.JsonUtils;
import src.islab1jee.model.coordinates.Coordinates;
import src.islab1jee.model.location.Location;
import src.islab1jee.model.movie.Movie;
import src.islab1jee.model.person.Person;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ImportService {

    @Inject
    private ImportRepository importRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public ImportOperation processImport(InputStream inputStream) {
        ImportOperation op = new ImportOperation();

        try {
            List<JsonObject> objects = JsonUtils.parseJsonArray(inputStream);
            int savedCount = 0;

            for (JsonObject jsonObj : objects) {
                String type = jsonObj.getString("type", "");

                switch (type) {
                    case "Movie":
                        if (persistMovie(jsonObj)) savedCount++;
                        break;
                    case "Coordinates":
                        if (persistCoordinates(jsonObj)) savedCount++;
                        break;
                    case "Location":
                        if (persistLocation(jsonObj)) savedCount++;
                        break;
                    default:
                        // Если type не указан, пытаемся угадать
                        if (jsonObj.containsKey("coordinates") && jsonObj.containsKey("director")) {
                            if (persistMovie(jsonObj)) savedCount++;
                        }
                        break;
                }
            }

            op.setStatus(ImportStatus.SUCCESS);
            op.setAddedCount(savedCount);
            op.setErrorMessage(null);

        } catch (Exception e) {
            op.setStatus(ImportStatus.FAILED);
            op.setAddedCount(0);
            op.setErrorMessage(e.getMessage());
        }

        importRepository.save(op);
        return op;
    }

    private boolean persistMovie(JsonObject json) {
        try {
            Coordinates coords = buildCoordinates(json.getJsonObject("coordinates"));
            em.persist(coords);

            Person director = buildPerson(json.getJsonObject("director"));
            em.persist(director);

            Person screenwriter = null;
            if (json.containsKey("screenwriter") && !json.isNull("screenwriter")) {
                screenwriter = buildPerson(json.getJsonObject("screenwriter"));
                em.persist(screenwriter);
            }

            Person operator = buildPerson(json.getJsonObject("operator"));
            em.persist(operator);

            Movie movie = new Movie();
            movie.setName(json.getString("name"));
            movie.setCoordinates(coords);
            movie.setCreationDate(LocalDate.now());
            movie.setOscarsCount(json.getJsonNumber("oscarsCount").longValue());
            if (json.containsKey("budget") && !json.isNull("budget")) {
                movie.setBudget(json.getJsonNumber("budget").longValue());
            }
            movie.setTotalBoxOffice(json.getJsonNumber("totalBoxOffice").longValue());
            movie.setMpaaRating(Enum.valueOf(src.islab1jee.enums.MpaaRating.class, json.getString("mpaaRating", "G")));
            movie.setDirector(director);
            movie.setScreenwriter(screenwriter);
            movie.setOperator(operator);
            movie.setLength(json.getJsonNumber("length").longValue());
            movie.setGoldenPalmCount(json.getInt("goldenPalmCount"));
            movie.setUsaBoxOffice(json.getJsonNumber("usaBoxOffice").doubleValue());
            movie.setTagline(json.getString("tagline"));
            movie.setGenre(Enum.valueOf(src.islab1jee.enums.MovieGenre.class, json.getString("genre")));

            em.persist(movie);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean persistCoordinates(JsonObject json) {
        try {
            if (json.containsKey("objects") && json.get("objects").getValueType() == jakarta.json.JsonValue.ValueType.ARRAY) {
                JsonArray arr = json.getJsonArray("objects");
                for (var val : arr) {
                    if (val.getValueType() == jakarta.json.JsonValue.ValueType.OBJECT) {
                        JsonObject obj = val.asJsonObject();
                        Coordinates coords = new Coordinates();
                        coords.setX((float) obj.getJsonNumber("x").doubleValue());
                        coords.setY(obj.getJsonNumber("y").longValue());
                        em.persist(coords);
                    }
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

    private Coordinates buildCoordinates(JsonObject json) {
        Coordinates coords = new Coordinates();
        coords.setX((float) json.getJsonNumber("x").doubleValue());
        coords.setY(json.getJsonNumber("y").longValue());
        return coords;
    }

    private boolean persistLocation(JsonObject json) {
        try {
            if (json.containsKey("objects") && json.get("objects").getValueType() == jakarta.json.JsonValue.ValueType.ARRAY) {
                JsonArray arr = json.getJsonArray("objects");
                for (var val : arr) {
                    if (val.getValueType() == jakarta.json.JsonValue.ValueType.OBJECT) {
                        Location loc = buildLocation(val.asJsonObject());
                        em.persist(loc);
                    }
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

    private Location buildLocation(JsonObject json) {
        return Location.builder()
                .x((float) json.getJsonNumber("x").doubleValue())
                .y(json.getJsonNumber("y").doubleValue())
                .z(json.getJsonNumber("z").longValue())
                .name(json.getString("name"))
                .build();
    }

    private Person buildPerson(JsonObject json) {
        Location loc = null;
        if (json.containsKey("location") && !json.isNull("location")) {
            loc = buildLocation(json.getJsonObject("location"));
            em.persist(loc);
        }

        Person person = new Person();
        person.setName(json.getString("name"));
        if (json.containsKey("eyeColor") && !json.isNull("eyeColor")) {
            person.setEyeColor(Enum.valueOf(src.islab1jee.enums.Color.class, json.getString("eyeColor")));
        }
        person.setHairColor(Enum.valueOf(src.islab1jee.enums.Color.class, json.getString("hairColor")));
        person.setLocation(loc);
        person.setWeight(json.getJsonNumber("weight").doubleValue());
        person.setPassportID(json.getString("passportID"));
        if (json.containsKey("nationality") && !json.isNull("nationality")) {
            person.setNationality(Enum.valueOf(src.islab1jee.enums.Country.class, json.getString("nationality")));
        }

        return person;
    }
}
