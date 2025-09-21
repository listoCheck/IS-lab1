package src.islab1jee.models.coordinates;


import src.islab1jee.DTO.CoordinatesRequestDto;
import src.islab1jee.DTO.CoordinatesResponseDto;
import src.islab1jee.models.entity.Coordinates;

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

