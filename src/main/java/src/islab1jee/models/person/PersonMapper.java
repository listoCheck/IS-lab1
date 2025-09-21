package src.islab1jee.models.person;
import src.islab1jee.models.entity.Location;
import src.islab1jee.models.entity.Person;
import src.islab1jee.models.location.LocationMapper;
import src.islab1jee.models.person.dto.PersonRequestDto;
import src.islab1jee.models.person.dto.PersonResponseDto;

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
                entity.getWeight(),
                entity.getPassportID(),
                entity.getNationality(),
                LocationMapper.toDto(entity.getLocation())
        );
    }
}

