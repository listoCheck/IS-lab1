package src.islab1jee.mapper;
import src.islab1jee.model.location.Location;
import src.islab1jee.model.person.Person;
import src.islab1jee.model.person.DTO.PersonRequestDto;
import src.islab1jee.model.person.DTO.PersonResponseDto;

public class PersonMapper {

    public static Person toEntity(PersonRequestDto dto, Location location) {
        Person p = new Person();
        p.setName(dto.getName());
        p.setEyeColor(dto.getEyeColor());
        p.setHairColor(dto.getHairColor());
        p.setLocation(location);
        p.setWeight(dto.getWeight());
        p.setPassportID(dto.getPassportID());
        p.setNationality(dto.getNationality());
        return p;
    }

    public static PersonResponseDto toDto(Person entity) {
        return new PersonResponseDto(
                        entity.getId(),
                        entity.getName(),
                        entity.getEyeColor(),
                        entity.getHairColor(),
                LocationMapper.toDto(entity.getLocation()),
                entity.getWeight(),
                entity.getPassportID(),
                entity.getNationality()
                );
    }
}

