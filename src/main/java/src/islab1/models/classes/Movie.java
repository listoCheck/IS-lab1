package src.islab1.models.classes;

import lombok.Getter;
import lombok.Setter;
import src.islab1.models.enums.MovieGenre;
import src.islab1.models.enums.MpaaRating;

import java.time.LocalDate;

@Setter
@Getter
public class Movie {
    private Integer id; // Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private LocalDate creationDate; // Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long oscarsCount; // Значение поля должно быть больше 0
    private Long budget; // Значение поля должно быть больше 0, Поле может быть null
    private long totalBoxOffice; // Значение поля должно быть больше 0
    private MpaaRating mpaaRating; // Поле может быть null
    private Person director; // Поле не может быть null
    private Person screenwriter; // Поле может быть null (судя по описанию)
    private Person operator; // Поле не может быть null
    private Long length; // Поле может быть null, Значение поля должно быть больше 0
    private int goldenPalmCount; // Значение поля должно быть больше 0
    private double usaBoxOffice; // Значение поля должно быть больше 0
    private String tagline; // Поле не может быть null
    private MovieGenre genre; // Поле не может быть null

    public Movie(Integer id, String name, Coordinates coordinates, LocalDate creationDate,
                 long oscarsCount, Long budget, long totalBoxOffice, MpaaRating mpaaRating,
                 Person director, Person screenwriter, Person operator, Long length,
                 int goldenPalmCount, double usaBoxOffice, String tagline, MovieGenre genre) {

        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.oscarsCount = oscarsCount;
        this.budget = budget;
        this.totalBoxOffice = totalBoxOffice;
        this.mpaaRating = mpaaRating;
        this.director = director;
        this.screenwriter = screenwriter;
        this.operator = operator;
        this.length = length;
        this.goldenPalmCount = goldenPalmCount;
        this.usaBoxOffice = usaBoxOffice;
        this.tagline = tagline;
        this.genre = genre;
    }

    public Movie() {
    }

    public boolean isValid() {
        return id != null && id > 0 &&
                name != null && !name.trim().isEmpty() &&
                coordinates != null &&
                creationDate != null &&
                oscarsCount > 0 &&
                (budget == null || budget > 0) &&
                totalBoxOffice > 0 &&
                director != null &&
                operator != null &&
                (length == null || length > 0) &&
                goldenPalmCount > 0 &&
                usaBoxOffice > 0 &&
                tagline != null &&
                genre != null;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", genre=" + genre +
                ", oscarsCount=" + oscarsCount +
                '}';
    }
}