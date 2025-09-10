package src.islab1.models.classes.location;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    @Transactional
    public Location save(Location movie) {
        return locationRepository.save(movie);
    }

    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    public Location findById(Integer id) {
        return locationRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void delete(Integer id) {
        locationRepository.deleteById(id);
    }
}

