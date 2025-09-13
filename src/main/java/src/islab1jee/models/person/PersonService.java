package src.islab1jee.models.person;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.annotation.ManagedProperty;
import src.islab1jee.models.location.Location;
import src.islab1jee.models.location.LocationRepository;
import src.islab1jee.models.person.dto.PersonRequestDto;
import src.islab1jee.models.person.dto.PersonResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@ManagedBean("personService")
@RequestScoped
public class PersonService {

    @ManagedProperty(value = "#{personRepository}")
    private PersonRepository repository;

    @ManagedProperty(value = "#{locationRepository}")
    private LocationRepository locationRepository;

    public void setRepository(PersonRepository repository) {
        this.repository = repository;
    }

    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

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

    public void delete(Integer id) {
        repository.delete(id);
    }
}
