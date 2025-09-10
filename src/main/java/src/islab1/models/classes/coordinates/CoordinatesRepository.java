package src.islab1.models.classes.coordinates;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoordinatesRepository extends JpaRepository<Coordinates, Integer> {
    //Optional<Coordinates> findById(int id);
}
