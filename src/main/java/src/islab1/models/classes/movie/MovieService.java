package src.islab1.models.classes.movie;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class MovieService {
    private final MovieRepository repository;

    @Transactional
    public Movie save(Movie movie) {
        return repository.save(movie);
    }

    public List<Movie> findAll() {
        return repository.findAll();
    }

    public Movie findById(Integer id) {
        return repository.findById(id).orElseThrow();
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}

