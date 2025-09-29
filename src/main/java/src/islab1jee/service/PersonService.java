package src.islab1jee.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import src.islab1jee.DTO.LocationResponseDto;
import src.islab1jee.enums.MovieGenre;
import src.islab1jee.mapper.LocationMapper;
import src.islab1jee.mapper.PersonMapper;
import src.islab1jee.entity.Location;
import src.islab1jee.entity.Person;
import src.islab1jee.repository.LocationRepository;
import src.islab1jee.DTO.PersonRequestDto;
import src.islab1jee.DTO.PersonResponseDto;
import src.islab1jee.repository.PersonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class PersonService {

    @Inject
    private PersonRepository repository;

    @Inject
    private LocationRepository locationRepository;

    @PersistenceContext
    private EntityManager em;

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

    public List<PersonResponseDto> getPaged(int page, int size) {
        List<Person> all = repository.findAll();
        int fromIndex = page * size;
        if (fromIndex >= all.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + size, all.size());

        return all.subList(fromIndex, toIndex).stream()
                .map(PersonMapper::toDto)
                .collect(Collectors.toList());
    }

}

