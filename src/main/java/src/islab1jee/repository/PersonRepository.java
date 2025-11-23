package src.islab1jee.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import src.islab1jee.model.person.Person;
import src.islab1jee.utils.DBCPDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PersonRepository {

    private final EntityManagerFactory emf;

    public PersonRepository() {
        DataSource ds = DBCPDataSource.getDataSource();
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.nonJtaDataSource", ds);
        props.put("hibernate.show_sql", true);
        props.put("hibernate.format_sql", true);
        props.put("jakarta.persistence.schema-generation.database.action", "update");

        emf = jakarta.persistence.Persistence.createEntityManagerFactory("PostgresPU", props);
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public Person save(Person p) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (p.getId() == null) {
                em.persist(p);
            } else {
                p = em.merge(p);
            }
            em.getTransaction().commit();
            return p;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Person findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Person.class, id);
        } finally {
            em.close();
        }
    }

    public List<Person> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Person p", Person.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Person p = em.find(Person.class, id);
            if (p != null) {
                em.remove(p);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Person> findPaged(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Person p", Person.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void close() {
        emf.close();
    }
}
