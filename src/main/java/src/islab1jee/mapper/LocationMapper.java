package src.islab1jee.mapper;

import src.islab1jee.model.location.DTO.LocationRequestDto;
import src.islab1jee.model.location.DTO.LocationResponseDto;
import src.islab1jee.model.location.Location;

public class LocationMapper {
    public static Location toEntity(LocationRequestDto dto) {
        Location l = new Location();
        l.setX(dto.getX());
        l.setY(dto.getY());
        l.setZ(dto.getZ());
        l.setName(dto.getName());
        return l;
    }

    public static LocationResponseDto toDto(Location entity) {
        return new LocationResponseDto(
                entity.getId(),
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                entity.getName()
        );
    }
}
