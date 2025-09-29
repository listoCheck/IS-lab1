package src.islab1jee.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import src.islab1jee.DTO.CoordinatesResponseDto;
import src.islab1jee.enums.MovieGenre;
import src.islab1jee.mapper.CoordinatesMapper;
import src.islab1jee.mapper.MovieMapper;
import src.islab1jee.entity.Coordinates;
import src.islab1jee.repository.CoordinatesRepository;
import src.islab1jee.entity.Movie;
import src.islab1jee.DTO.MovieRequestDto;
import src.islab1jee.DTO.MovieResponseDto;
import src.islab1jee.entity.Person;
import src.islab1jee.repository.MovieRepository;
import src.islab1jee.repository.PersonRepository;

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

    public List<MovieResponseDto> getPaged(int page, int size) {
        List<Movie> all = movieRepository.findAll();
        int fromIndex = page * size;
        if (fromIndex >= all.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + size, all.size());

        return all.subList(fromIndex, toIndex).stream().map(MovieMapper::toDto).collect(Collectors.toList());
    }

    public Double getMiddle() {
        List<Movie> all = movieRepository.findAll();
        return all.stream().mapToDouble(Movie::getUsaBoxOffice).average().orElse(0.0);
    }

    public Integer countByGenre(MovieGenre genre) {
        List<Movie> all = movieRepository.findAll();
        return (int) all.stream()
                .filter(movie -> movie.getGenre() == genre)
                .count();
    }

    public List<MovieResponseDto> getByTagline(int page, int size, int tagline){
        List<Movie> all = movieRepository.findAll();
        List<Movie> filtered = all.stream()
                .filter(m -> m.getTagline() != null && m.getTagline().length() > tagline)
                .toList();
        int fromIndex = page * size;
        if (fromIndex >= filtered.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + size, filtered.size());

        return filtered.subList(fromIndex, toIndex).stream().map(MovieMapper::toDto).collect(Collectors.toList());
    }
    public List<MovieResponseDto> getByOscars(int page, int size){
        List<Movie> all = movieRepository.findAll();
        List<Movie> filtered = all.stream()
                .filter(m -> m.getTagline() != null && m.getOscarsCount() == 0)
                .toList();
        int fromIndex = page * size;
        if (fromIndex >= filtered.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + size, filtered.size());

        return filtered.subList(fromIndex, toIndex).stream().map(MovieMapper::toDto).collect(Collectors.toList());
    }

    public void deleteOscarsByGenre(String genre) {
        movieRepository.deleteOscarsByGenre(genre);
    }
}
