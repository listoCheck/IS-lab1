package src.islab1.models.classes.movie;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("api/movies")
public class MovieController {
    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @PostMapping
    public Movie create(@Valid @RequestBody Movie movie) {
        return service.save(movie);
    }

    @GetMapping
    public List<Movie> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Movie getOne(@PathVariable Integer id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
