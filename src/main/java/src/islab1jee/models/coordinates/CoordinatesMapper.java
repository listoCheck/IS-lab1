package src.islab1jee.models.coordinates;


import src.islab1jee.models.coordinates.dto.*;

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

