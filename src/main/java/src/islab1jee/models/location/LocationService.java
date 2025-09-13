package src.islab1jee.models.location;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.annotation.ManagedProperty;
import src.islab1jee.models.location.dto.*;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean("locationService")
@RequestScoped
public class LocationService {

    @ManagedProperty(value = "#{locationRepository}")
    private LocationRepository repository;

    public void setRepository(LocationRepository repository) {
        this.repository = repository;
    }

    public LocationResponseDto create(LocationRequestDto dto) {
        Location entity = LocationMapper.toEntity(dto);
        Location saved = repository.save(entity);
        return LocationMapper.toDto(saved);
    }

    public LocationResponseDto getById(Integer id) {
        Location entity = repository.findById(id);
        if (entity == null) {
            throw new RuntimeException("Локация с id=" + id + " не найдена");
        }
        return LocationMapper.toDto(entity);
    }

    public List<LocationResponseDto> getAll() {
        return repository.findAll().stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());
    }

    public LocationResponseDto update(Integer id, LocationRequestDto dto) {
        Location entity = repository.findById(id);
        if (entity == null) {
            throw new RuntimeException("Локация с id=" + id + " не найдена");
        }
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        entity.setZ(dto.getZ());
        entity.setName(dto.getName());
        Location updated = repository.save(entity);
        return LocationMapper.toDto(updated);
    }

    public void delete(Integer id) {
        repository.delete(id);
    }
}
