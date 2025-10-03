package src.islab1jee.model.movie.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import src.islab1jee.model.person.DTO.PersonResponseDto;
import src.islab1jee.enums.MovieGenre;
import src.islab1jee.enums.MpaaRating;
import src.islab1jee.model.coordinates.DTO.CoordinatesResponseDto;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponseDto {
    private Integer id;
    private String name;
    private CoordinatesResponseDto coordinates;
    private LocalDate creationDate;
    private long oscarsCount;
    private Long budget;
    private long totalBoxOffice;
    private MpaaRating mpaaRating;
    private PersonResponseDto director;
    private PersonResponseDto screenwriter;
    private PersonResponseDto operator;
    private Long length;
    private int goldenPalmCount;
    private double usaBoxOffice;
    private String tagline;
    private MovieGenre genre;


}
