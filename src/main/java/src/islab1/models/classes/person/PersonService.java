package src.islab1.models.classes.person;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class PersonService {
    final PersonRepository personRepository;

    public Person edit(Integer id, Person newPerson) {
        Person person = personRepository.getReferenceById(id);
        person.setName(newPerson.getName());
        person.setEyeColor(newPerson.getEyeColor());
        person.setHairColor(newPerson.getHairColor());
        person.setLocation(newPerson.getLocation());
        person.setWeight(newPerson.getWeight());
        person.setPassportID(newPerson.getPassportID());
        person.setNationality(newPerson.getNationality());
        return personRepository.save(person);
    }
    public Person findById(int id) {
        return personRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Coordinates " + id + " not found")
        );
    }

    public void deleteById(Integer id) {
        personRepository.deleteById(id);
    }

    public void save(Person person) {
        personRepository.save(person);
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }
}
