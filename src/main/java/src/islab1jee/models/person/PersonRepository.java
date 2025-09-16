package src.islab1jee.models.person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class PersonRepository {

    @PersistenceContext(unitName = "PostgresPU")
    private EntityManager em;

    public Person save(Person p) {
        if (p.getId() == null) {
            em.persist(p);
            return p;
        }
        return em.merge(p);
    }

    public Person findById(Integer id) {
        return em.find(Person.class, id);
    }

    public List<Person> findAll() {
        return em.createQuery("SELECT p FROM Person p", Person.class).getResultList();
    }

    public void delete(Integer id) {
        Person p = findById(id);
        if (p != null) {
            em.remove(p);
        }
    }
}
