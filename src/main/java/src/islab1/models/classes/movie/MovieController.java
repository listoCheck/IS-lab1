package src.islab1.models.classes.movie;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import src.islab1.models.classes.movie.DTO.MovieResponseDTO;
import src.islab1.models.classes.movie.DTO.MovieRequestDTO;

import java.util.List;

@RestController
@RequestMapping("api/movies")
public class MovieController {
    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @PostMapping
    public MovieResponseDTO create(@Valid @RequestBody MovieRequestDTO dto) {
        Movie movie = MovieMapper.toEntity(dto);
        return MovieMapper.toDto(service.save(movie));
    }

    @GetMapping
    public List<MovieResponseDTO> getAll() {
        return service.findAll().stream()
                .map(MovieMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public MovieResponseDTO getOne(@PathVariable Integer id) {
        return MovieMapper.toDto(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
