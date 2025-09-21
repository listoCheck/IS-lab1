package src.islab1jee.models.person;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import src.islab1jee.models.entity.Location;
import src.islab1jee.models.entity.Person;
import src.islab1jee.models.location.LocationRepository;
import src.islab1jee.DTO.PersonRequestDto;
import src.islab1jee.DTO.PersonResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class PersonService {

    @Inject
    private PersonRepository repository;

    @Inject
    private LocationRepository locationRepository;

    @Transactional
    public PersonResponseDto create(PersonRequestDto dto) {
        Location location = locationRepository.findById(dto.getLocationId());
        if (location == null) {
            throw new RuntimeException("Локация с id=" + dto.getLocationId() + " не найдена");
        }
        Person entity = PersonMapper.toEntity(dto, location);
        return PersonMapper.toDto(repository.save(entity));
    }

    public PersonResponseDto getById(Integer id) {
        Person entity = repository.findById(id);
        if (entity == null) {
            throw new RuntimeException("Person с id=" + id + " не найден");
        }
        return PersonMapper.toDto(entity);
    }

    public List<PersonResponseDto> getAll() {
        return repository.findAll().stream()
                .map(PersonMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PersonResponseDto update(Integer id, PersonRequestDto dto) {
        Person entity = repository.findById(id);
        if (entity == null) {
            throw new RuntimeException("Person с id=" + id + " не найден");
        }

        Location location = locationRepository.findById(dto.getLocationId());
        if (location == null) {
            throw new RuntimeException("Локация с id=" + dto.getLocationId() + " не найдена");
        }

        entity.setName(dto.getName());
        entity.setEyeColor(dto.getEyeColor());
        entity.setHairColor(dto.getHairColor());
        entity.setLocation(location);
        entity.setWeight(dto.getWeight());
        entity.setPassportID(dto.getPassportID());
        entity.setNationality(dto.getNationality());

        return PersonMapper.toDto(repository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        repository.delete(id);
    }
}
