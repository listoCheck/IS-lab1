package src.islab1jee.repository;

import jakarta.enterprise.context.ApplicationScoped;
import src.islab1jee.model.person.Person;

@ApplicationScoped
public class PersonRepository extends GenericRepository<Person> {


    @Override
    protected Class<Person> getEntityClass() {
        return Person.class;
    }

}
