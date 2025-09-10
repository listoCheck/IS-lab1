package src.islab1.models.classes.movie;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    Page<Movie> findByTitle(String name);
    Page<Movie> findById(int id);
}

