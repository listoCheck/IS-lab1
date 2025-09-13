package src.islab1jee.models.coordinates;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import src.islab1jee.models.coordinates.dto.*;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "coordinatesService")
@RequestScoped
public class CoordinatesService {

    private CoordinatesRepository repository = new CoordinatesRepository();

    public CoordinatesResponseDto create(CoordinatesRequestDto dto) {
        Coordinates entity = CoordinatesMapper.toEntity(dto);
        Coordinates saved = repository.save(entity);
        return CoordinatesMapper.toDto(saved);
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
}
