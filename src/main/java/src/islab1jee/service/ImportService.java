package src.islab1jee.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import src.islab1jee.enums.*;
import src.islab1jee.model.coordinates.Coordinates;
import src.islab1jee.model.importobjects.ImportOperation;
import src.islab1jee.model.location.Location;
import src.islab1jee.model.movie.Movie;
import src.islab1jee.model.person.Person;
import src.islab1jee.repository.*;
import src.islab1jee.utils.S3Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@ApplicationScoped
public class ImportService {

    @Inject
    S3Service s3;

    @Inject
    ImportRepository importOperationRepo;


    private static final Logger logger = Logger.getLogger(ImportService.class.getName());
    private final CoordinatesRepository coordinatesRepo = new CoordinatesRepository();
    private final LocationRepository locationRepo = new LocationRepository();
    private final MovieRepository movieRepo = new MovieRepository();
    private final PersonRepository personRepo = new PersonRepository();

    public ImportOperation processImport(InputStream inputStream) {
        ImportOperation op = new ImportOperation();
        op.setTimestamp(LocalDateTime.now());
        op.setAddedCount(0);

        byte[] rawBytes;
        try {
            rawBytes = inputStream.readAllBytes();
        } catch (Exception e) {
            op.setStatus(src.islab1jee.enums.ImportStatus.FAILED);
            op.setErrorMessage("Ошибка чтения файла: " + e.getMessage());
            importOperationRepo.save(op);
            logger.severe("Не удалось прочитать входной поток: " + e.getMessage());
            return op;
        }

        String originalJsonText = new String(rawBytes, StandardCharsets.UTF_8).replace("\uFEFF", "").trim();
        StringBuilder sb = new StringBuilder();
        boolean insideJson = false;
        for (String line : originalJsonText.split("\\R")) {
            line = line.trim();
            if (line.startsWith("{") || line.startsWith("[")) insideJson = true;
            if (insideJson) sb.append(line).append("\n");
        }
        String jsonTextToSave = sb.toString().trim();
        String typeHint = "UnknownType";
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootPreview = mapper.readTree(jsonTextToSave);
            if (rootPreview.has("type")) typeHint = rootPreview.path("type").asText("UnknownType");
            else if (rootPreview.isArray() && rootPreview.size() > 0 && rootPreview.get(0).has("type"))
                typeHint = rootPreview.get(0).path("type").asText("UnknownType");
        } catch (Exception ex) {
            logger.info("Не удалось определить type: " + ex.getMessage());
        }

        String objectName = typeHint + "-import-" + UUID.randomUUID() + ".json";

        op.setStatus(src.islab1jee.enums.ImportStatus.PREPARING);
        importOperationRepo.save(op);

        try {
            String s3Url = s3.uploadFile(objectName,
                    new ByteArrayInputStream(jsonTextToSave.getBytes(StandardCharsets.UTF_8)));
            op.setS3Url(s3Url);
            importOperationRepo.save(op);
        } catch (Exception uploadEx) {
            logger.severe("Ошибка при загрузке файла в S3: " + uploadEx.getMessage());
            op.setStatus(src.islab1jee.enums.ImportStatus.FAILED);
            op.setErrorMessage("Ошибка загрузки в S3: " + uploadEx.getMessage());
            importOperationRepo.save(op);
            return op;
        }

        boolean dbSuccess = false;
        try {
            int savedCount = persistObjects(jsonTextToSave, op);
            op.setAddedCount(savedCount);
            dbSuccess = true;
        } catch (Exception dbEx) {
            logger.severe("Ошибка БД: " + dbEx.getMessage());
            op.setErrorMessage("Ошибка при сохранении в БД: " + dbEx.getMessage());
        }

        if (dbSuccess) {
            op.setStatus(src.islab1jee.enums.ImportStatus.SUCCESS);
            importOperationRepo.save(op);
        } else {
            try {
                s3.deleteFile(objectName);
            } catch (Exception ignored) {
            }
            op.setStatus(src.islab1jee.enums.ImportStatus.FAILED);
            importOperationRepo.save(op);
        }

        return op;
    }

    private int persistObjects(String jsonText, ImportOperation op) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(jsonText);
        } catch (Exception e) {
            throw new RuntimeException("Невалидный JSON: " + e.getMessage(), e);
        }

        List<JsonNode> objects = new ArrayList<>();
        if (root.has("type") && root.has("objects") && root.get("objects").isArray()) {
            root.get("objects").forEach(objects::add);
        } else if (root.isArray()) {
            root.forEach(objects::add);
        } else {
            objects.add(root);
        }

        int savedCount = 0;
        String typeHint = root.path("type").asText("");

        for (JsonNode json : objects) {
            String type = json.has("type") ? json.path("type").asText() : typeHint;
            boolean persisted = switch (type) {
                case "Movie" -> persistMovie(json);
                case "Person" -> persistPerson(json);
                case "Coordinates" -> persistCoordinates(json);
                case "Location" -> persistLocation(json);
                default -> json.has("coordinates") && json.has("director") && persistMovie(json);
            };
            if (persisted) savedCount++;
        }

        op.setAddedCount(savedCount);
        return savedCount;
    }

    private boolean persistCoordinates(JsonNode json) {
        Coordinates coords = buildCoordinates(json);
        coordinatesRepo.save(coords);
        return true;
    }

    private Coordinates buildCoordinates(JsonNode json) {
        Coordinates coords = new Coordinates();
        coords.setX(json.path("x").floatValue());
        coords.setY(json.path("y").longValue());
        return coords;
    }

    private boolean persistLocation(JsonNode json) {
        Location loc = buildLocation(json);
        locationRepo.save(loc);
        return true;
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
        Person person = buildPerson(json);
        personRepo.save(person);
        return true;
    }

    private Person buildPerson(JsonNode json) {
        if (json == null || json.isNull() || json.isMissingNode())
            throw new IllegalArgumentException("Person node is null or missing");

        Location loc = null;
        if (json.has("location") && !json.get("location").isNull()) {
            loc = buildLocation(json.get("location"));
            locationRepo.save(loc);
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

    private boolean persistMovie(JsonNode json) {
        Coordinates coords = buildCoordinates(json.path("coordinates"));
        coordinatesRepo.save(coords);

        Person director = buildPerson(json.path("director"));
        personRepo.save(director);

        Person operator = buildPerson(json.path("operator"));
        personRepo.save(operator);

        Person screenwriter = null;
        if (json.has("screenwriter") && !json.get("screenwriter").isNull()) {
            screenwriter = buildPerson(json.get("screenwriter"));
            personRepo.save(screenwriter);
        }

        Movie movie = new Movie();
        movie.setName(json.path("name").asText(null));
        movie.setCoordinates(coords);
        movie.setCreationDate(LocalDate.now());
        movie.setOscarsCount(json.path("oscarsCount").asLong(0));
        if (json.has("budget") && !json.get("budget").isNull()) movie.setBudget(json.path("budget").asLong());
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

        movieRepo.save(movie);
        return true;
    }

    private <E extends Enum<E>> E parseEnum(JsonNode json, String field, Class<E> enumClass, boolean required) {
        if (!json.has(field) || json.get(field).isNull()) {
            if (required) throw new IllegalArgumentException("Поле '" + field + "' отсутствует или null");
            return null;
        }
        String text = json.get(field).asText();
        try {
            return Enum.valueOf(enumClass, text);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Недопустимое значение '" + text + "' для поля " + field + ", ожидается: " +
                            Arrays.toString(enumClass.getEnumConstants()));
        }
    }

    private void validateNotNull(Object obj, String field) {
        if (obj == null) throw new IllegalArgumentException("Поле " + field + " не может быть null");
    }
}
