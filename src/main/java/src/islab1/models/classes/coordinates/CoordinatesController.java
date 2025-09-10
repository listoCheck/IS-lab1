package src.islab1.models.classes.coordinates;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import src.islab1.models.classes.coordinates.dto.CoordinateDTO;

@RestController
@RequestMapping("/api/coordinates")
public class CoordinatesController {
    CoordinatesService coordinatesService;
    @GetMapping("/{id}")
    public ResponseEntity<Coordinates> getCoordinates(@PathVariable int id) {
        return ResponseEntity.ok(coordinatesService.findById(id));
    }
}
