package src.islab1jee.models.movie;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import src.islab1jee.models.coordinates.Coordinates;
import src.islab1jee.models.coordinates.CoordinatesRepository;
import src.islab1jee.models.movie.dto.MovieRequestDto;
import src.islab1jee.models.movie.dto.MovieResponseDto;
import src.islab1jee.models.person.Person;
import src.islab1jee.models.person.PersonRepository;

import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "movieService")
@RequestScoped
public class MovieService {

    @ManagedProperty(value = "#{movieRepository}")
    private MovieRepository movieRepository;

    @ManagedProperty(value = "#{coordinatesRepository}")
    private CoordinatesRepository coordinatesRepository;

    @ManagedProperty(value = "#{personRepository}")
    private PersonRepository personRepository;

    public void setMovieRepository(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public void setCoordinatesRepository(CoordinatesRepository coordinatesRepository) {
        this.coordinatesRepository = coordinatesRepository;
    }

    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

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

    public void delete(Integer id) {
        movieRepository.delete(id);
    }
}
