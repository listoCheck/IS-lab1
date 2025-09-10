package src.islab1.models.classes.coordinates;

import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CoordinatesService {
    final CoordinatesRepository coordinatesRepository;

    public Coordinates edit(Integer id, Coordinates newCoordinates) {
        Coordinates coordinates = coordinatesRepository.getReferenceById(id);
        coordinates.setX(newCoordinates.getX());
        coordinates.setY(newCoordinates.getY());
        return coordinatesRepository.save(coordinates);
    }
    public Coordinates findById(int id) {
        return coordinatesRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Coordinates " + id + " not found")
        );
    }

    public void deleteById(Integer id) {
        coordinatesRepository.deleteById(id);
    }
}
