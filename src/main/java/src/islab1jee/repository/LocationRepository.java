package src.islab1jee.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import src.islab1jee.model.location.Location;
import src.islab1jee.utils.DBCPDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LocationRepository {

    private final EntityManagerFactory emf;

    public LocationRepository() {
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

    public Location save(Location location) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (location.getId() == null) {
                em.persist(location);
            } else {
                location = em.merge(location);
            }
            em.getTransaction().commit();
            return location;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Location findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Location.class, id);
        } finally {
            em.close();
        }
    }

    public List<Location> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT l FROM Location l", Location.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Location location = em.find(Location.class, id);
            if (location != null) {
                em.remove(location);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Location> findPaged(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT l FROM Location l", Location.class)
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
