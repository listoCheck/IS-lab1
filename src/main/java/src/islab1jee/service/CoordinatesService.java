package src.islab1jee.service;

import jakarta.enterprise.context.ApplicationScoped;
import src.islab1jee.model.coordinates.DTO.CoordinatesRequestDto;
import src.islab1jee.model.coordinates.DTO.CoordinatesResponseDto;
import src.islab1jee.model.coordinates.Coordinates;
import src.islab1jee.repository.CoordinatesRepository;
import src.islab1jee.mapper.CoordinatesMapper;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CoordinatesService {

    private final CoordinatesRepository repository;

    public CoordinatesService() {
        this.repository = new CoordinatesRepository();
    }

    public CoordinatesResponseDto create(CoordinatesRequestDto dto) {
        Coordinates entity = CoordinatesMapper.toEntity(dto);
        Coordinates saved = repository.save(entity);
        return CoordinatesMapper.toDto(saved);
    }

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

    public List<CoordinatesResponseDto> getPaged(int page, int size) {
        return repository.findPaged(page, size)
                .stream()
                .map(CoordinatesMapper::toDto)
                .collect(Collectors.toList());
    }
}
