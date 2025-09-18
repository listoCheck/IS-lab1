package src.islab1jee.models.coordinates;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import src.islab1jee.models.coordinates.dto.*;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CoordinatesService {

    @Inject
    private CoordinatesRepository repository;

    @Transactional
    public CoordinatesResponseDto create(CoordinatesRequestDto dto) {
        Coordinates entity = CoordinatesMapper.toEntity(dto);
        Coordinates saved = repository.save(entity);
        return CoordinatesMapper.toDto(saved);
    }

    @Transactional
    public CoordinatesResponseDto update(Integer id, CoordinatesRequestDto dto) {
        Coordinates entity = repository.findById(id);
        if (entity == null) {
            throw new RuntimeException("Координаты с id=" + id + " не найдены");
        }
        entity.setX(dto.getX());
        entity.setY(dto.getY());
        Coordinates updated = repository.save(entity);
        return CoordinatesMapper.toDto(updated);
    }

    @Transactional
    public void delete(Integer id) {
        repository.delete(id);
    }

    public CoordinatesResponseDto getById(Integer id) {
        Coordinates entity = repository.findById(id);
        if (entity == null) {
            throw new RuntimeException("Координаты с id=" + id + " не найдены");
        }
        return CoordinatesMapper.toDto(entity);
    }

    public List<CoordinatesResponseDto> getAll() {
        return repository.findAll().stream()
                .map(CoordinatesMapper::toDto)
                .collect(Collectors.toList());
    }

}
