package src.islab1jee.models.movie;

import src.islab1jee.models.coordinates.Coordinates;
import src.islab1jee.models.coordinates.CoordinatesMapper;
import src.islab1jee.models.movie.dto.MovieRequestDto;
import src.islab1jee.models.movie.dto.MovieResponseDto;
import src.islab1jee.models.person.Person;
import src.islab1jee.models.person.PersonMapper;

import java.time.LocalDate;

public class MovieMapper {

    public static Movie toEntity(MovieRequestDto dto, Coordinates coordinates,
                                 Person director, Person screenwriter, Person operator) {
        Movie m = new Movie();
        m.setName(dto.getName());
        m.setCoordinates(coordinates);
        m.setCreationDate(LocalDate.now());
        m.setOscarsCount(dto.getOscarsCount());
        m.setBudget(dto.getBudget());
        m.setTotalBoxOffice(dto.getTotalBoxOffice());
        m.setMpaaRating(dto.getMpaaRating());
        m.setDirector(director);
        m.setScreenwriter(screenwriter);
        m.setOperator(operator);
        m.setLength(dto.getLength());
        m.setGoldenPalmCount(dto.getGoldenPalmCount());
        m.setUsaBoxOffice(dto.getUsaBoxOffice());
        m.setTagline(dto.getTagline());
        m.setGenre(dto.getGenre());
        return m;
    }

    public static MovieResponseDto toDto(Movie movie) {
        return new MovieResponseDto(
                movie.getId(),
                movie.getName(),
                CoordinatesMapper.toDto(movie.getCoordinates()),
                movie.getCreationDate(),
                movie.getOscarsCount(),
                movie.getBudget(),
                movie.getTotalBoxOffice(),
                movie.getMpaaRating(),
                PersonMapper.toDto(movie.getDirector()),
                movie.getScreenwriter() != null ? PersonMapper.toDto(movie.getScreenwriter()) : null,
                PersonMapper.toDto(movie.getOperator()),
                movie.getLength(),
                movie.getGoldenPalmCount(),
                movie.getUsaBoxOffice(),
                movie.getTagline(),
                movie.getGenre()
        );
    }
}

