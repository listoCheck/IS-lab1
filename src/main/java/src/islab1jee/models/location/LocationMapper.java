package src.islab1jee.models.location;

import src.islab1jee.DTO.LocationRequestDto;
import src.islab1jee.DTO.LocationResponseDto;
import src.islab1jee.models.entity.Location;

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
