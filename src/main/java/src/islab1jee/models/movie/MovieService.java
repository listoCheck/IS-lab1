package src.islab1jee.models.movie;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import src.islab1jee.mapper.MovieMapper;
import src.islab1jee.models.entity.Coordinates;
import src.islab1jee.models.coordinates.CoordinatesRepository;
import src.islab1jee.models.entity.Movie;
import src.islab1jee.DTO.MovieRequestDto;
import src.islab1jee.DTO.MovieResponseDto;
import src.islab1jee.models.entity.Person;
import src.islab1jee.models.person.PersonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class MovieService {

    @Inject
    private MovieRepository movieRepository;

    @Inject
    private CoordinatesRepository coordinatesRepository;

    @Inject
    private PersonRepository personRepository;

    @Transactional
    public MovieResponseDto create(MovieRequestDto dto) {
        Coordinates coordinates = coordinatesRepository.findById(dto.getCoordinatesId());
        Person director = personRepository.findById(dto.getDirectorId());
        Person screenwriter = dto.getScreenwriterId() != null ? personRepository.findById(dto.getScreenwriterId()) : null;
        Person operator = personRepository.findById(dto.getOperatorId());

        if (coordinates == null || director == null || operator == null) {
            throw new RuntimeException("Некорректные id связанных объектов");
        }

        Movie movie = MovieMapper.toEntity(dto, coordinates, director, screenwriter, operator);
        return MovieMapper.toDto(movieRepository.save(movie));
    }

    public MovieResponseDto getById(Integer id) {
        Movie movie = movieRepository.findById(id);
        if (movie == null) throw new RuntimeException("Фильм с id=" + id + " не найден");
        return MovieMapper.toDto(movie);
    }

    public List<MovieResponseDto> getAll() {
        return movieRepository.findAll().stream()
                .map(MovieMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MovieResponseDto update(Integer id, MovieRequestDto dto) {
        Movie movie = movieRepository.findById(id);
        if (movie == null) throw new RuntimeException("Фильм с id=" + id + " не найден");

        Coordinates coordinates = coordinatesRepository.findById(dto.getCoordinatesId());
        Person director = personRepository.findById(dto.getDirectorId());
        Person screenwriter = dto.getScreenwriterId() != null ? personRepository.findById(dto.getScreenwriterId()) : null;
        Person operator = personRepository.findById(dto.getOperatorId());

        if (coordinates == null || director == null || operator == null) {
            throw new RuntimeException("Некорректные id связанных объектов");
        }

        movie.setName(dto.getName());
        movie.setCoordinates(coordinates);
        movie.setOscarsCount(dto.getOscarsCount());
        movie.setBudget(dto.getBudget());
        movie.setTotalBoxOffice(dto.getTotalBoxOffice());
        movie.setMpaaRating(dto.getMpaaRating());
        movie.setDirector(director);
        movie.setScreenwriter(screenwriter);
        movie.setOperator(operator);
        movie.setLength(dto.getLength());
        movie.setGoldenPalmCount(dto.getGoldenPalmCount());
        movie.setUsaBoxOffice(dto.getUsaBoxOffice());
        movie.setTagline(dto.getTagline());
        movie.setGenre(dto.getGenre());

        return MovieMapper.toDto(movieRepository.save(movie));
    }

    @Transactional
    public void delete(Integer id) {
        movieRepository.delete(id);
    }
}
