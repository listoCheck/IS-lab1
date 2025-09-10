package src.islab1.models.classes.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.islab1.models.classes.coordinates.Coordinates;

@Repository
public interface LocationRepository  extends JpaRepository<Location, Integer> {
}
