package src.islab1.models.classes.movie.DTO;

import jakarta.validation.constraints.*;
import lombok.*;
import src.islab1.models.enums.MovieGenre;
import src.islab1.models.enums.MpaaRating;
import src.islab1.models.classes.coordinates.Coordinates;
import src.islab1.models.classes.person.Person;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieRequestDTO {

    @NotBlank
    private String name;

    @NotNull
    private Coordinates coordinates;

    @Positive
    private long oscarsCount;

    @Positive
    private Long budget; // может быть null

    @Positive
    private long totalBoxOffice;

    private MpaaRating mpaaRating;

    @NotNull
    private Person director;

    private Person screenwriter;

    @NotNull
    private Person operator;

    @Positive
    private Long length;

    @Positive
    private int goldenPalmCount;

    @Positive
    private double usaBoxOffice;

    @NotBlank
    private String tagline;

    @NotNull
    private MovieGenre genre;
}
