package src.islab1.models.classes.coordinates;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.islab1.models.classes.coordinates.dto.CoordinateDTO;

@RestController
@RequestMapping("/api/coordinates")
public class CoordinatesController {
    CoordinatesService coordinatesService;
    @GetMapping("/{id}")
    public ResponseEntity<Coordinates> getCoordinates(@PathVariable int id) {
        return ResponseEntity.ok(coordinatesService.findById(id));
    }
    @PostMapping("/save")
    public void saveCoordinates(@RequestBody Coordinates coordinates) {
        coordinatesService.save(coordinates);
    }
    @DeleteMapping("/{id}")
    public void deleteCoordinates(@PathVariable int id) {
        coordinatesService.deleteById(id);
    }
}
