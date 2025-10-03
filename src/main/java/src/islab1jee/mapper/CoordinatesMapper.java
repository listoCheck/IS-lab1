package src.islab1jee.mapper;


import src.islab1jee.model.coordinates.DTO.CoordinatesRequestDto;
import src.islab1jee.model.coordinates.DTO.CoordinatesResponseDto;
import src.islab1jee.model.coordinates.Coordinates;

public class CoordinatesMapper {
    public static Coordinates toEntity(CoordinatesRequestDto dto) {
        Coordinates c = new Coordinates();
        c.setX(dto.getX());
        c.setY(dto.getY());
        return c;
    }

    public static CoordinatesResponseDto toDto(Coordinates entity) {
        return new CoordinatesResponseDto(entity.getId(), entity.getX(), entity.getY());
    }
}

