package src.islab1.models.classes.movie.DTO;

import lombok.*;
import src.islab1.models.enums.MovieGenre;
import src.islab1.models.enums.MpaaRating;
import src.islab1.models.classes.coordinates.Coordinates;
import src.islab1.models.classes.person.Person;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponseDTO {
    private Integer id;
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate;
    private long oscarsCount;
    private Long budget;
    private long totalBoxOffice;
    private MpaaRating mpaaRating;
    private Person director;
    private Person screenwriter;
    private Person operator;
    private Long length;
    private int goldenPalmCount;
    private double usaBoxOffice;
    private String tagline;
    private MovieGenre genre;
}
