package src.islab1jee.models.movie.dto;

import lombok.Getter;
import lombok.Setter;
import src.islab1jee.enums.MovieGenre;
import src.islab1jee.enums.MpaaRating;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Setter
@Getter
public class MovieRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private Integer coordinatesId;

    @Positive
    private long oscarsCount;

    private Long budget;

    @Positive
    private long totalBoxOffice;

    private MpaaRating mpaaRating;

    @NotNull
    private Integer directorId;

    private Integer screenwriterId;

    @NotNull
    private Integer operatorId;

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
