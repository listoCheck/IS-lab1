package src.islab1jee.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import src.islab1jee.DTO.CoordinatesResponseDto;
import src.islab1jee.DTO.LocationRequestDto;
import src.islab1jee.DTO.LocationResponseDto;
import src.islab1jee.entity.Coordinates;
import src.islab1jee.mapper.CoordinatesMapper;
import src.islab1jee.mapper.LocationMapper;
import src.islab1jee.entity.Location;
import src.islab1jee.repository.LocationRepository;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class LocationService {

    @Inject
    private LocationRepository repository;

    @Transactional
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

    @Transactional
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

    @Transactional
    public void delete(Integer id) {
        repository.delete(id);
    }

    public List<LocationResponseDto> getPaged(int page, int size) {
        List<Location> all = repository.findAll();
        int fromIndex = page * size;
        if (fromIndex >= all.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + size, all.size());

        return all.subList(fromIndex, toIndex).stream()
                .map(LocationMapper::toDto)
                .collect(Collectors.toList());
    }
}
