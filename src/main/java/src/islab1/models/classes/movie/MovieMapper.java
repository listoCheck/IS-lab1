package src.islab1.models.classes.movie;
import src.islab1.models.classes.movie.DTO.MovieResponseDTO;
import src.islab1.models.classes.movie.DTO.MovieRequestDTO;


public class MovieMapper {
    public static Movie toEntity(MovieRequestDTO dto) {
        return Movie.builder()
                .name(dto.getName())
                .coordinates(dto.getCoordinates())
                .oscarsCount(dto.getOscarsCount())
                .budget(dto.getBudget())
                .totalBoxOffice(dto.getTotalBoxOffice())
                .mpaaRating(dto.getMpaaRating())
                .director(dto.getDirector())
                .screenwriter(dto.getScreenwriter())
                .operator(dto.getOperator())
                .length(dto.getLength())
                .goldenPalmCount(dto.getGoldenPalmCount())
                .usaBoxOffice(dto.getUsaBoxOffice())
                .tagline(dto.getTagline())
                .genre(dto.getGenre())
                .build();
    }

    public static MovieResponseDTO toDto(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .name(movie.getName())
                .coordinates(movie.getCoordinates())
                .creationDate(movie.getCreationDate())
                .oscarsCount(movie.getOscarsCount())
                .budget(movie.getBudget())
                .totalBoxOffice(movie.getTotalBoxOffice())
                .mpaaRating(movie.getMpaaRating())
                .director(movie.getDirector())
                .screenwriter(movie.getScreenwriter())
                .operator(movie.getOperator())
                .length(movie.getLength())
                .goldenPalmCount(movie.getGoldenPalmCount())
                .usaBoxOffice(movie.getUsaBoxOffice())
                .tagline(movie.getTagline())
                .genre(movie.getGenre())
                .build();
    }
}
