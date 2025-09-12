package src.islab1.models.classes.person;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/person")
public class PersonController {
    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @PostMapping
    public void create(@Valid @RequestBody Person person) {
        service.save(person);
    }

    @GetMapping
    public List<Person> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Person getOne(@PathVariable Integer id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteById(id);
    }
}
