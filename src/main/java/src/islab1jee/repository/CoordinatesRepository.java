package src.islab1jee.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import src.islab1jee.model.coordinates.Coordinates;
import src.islab1jee.utils.DBCPDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoordinatesRepository {

    private final EntityManagerFactory emf;

    public CoordinatesRepository() {
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

    public Coordinates save(Coordinates c) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (c.getId() == null) {
                em.persist(c);
            } else {
                c = em.merge(c);
            }
            em.getTransaction().commit();
            return c;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Coordinates findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Coordinates.class, id);
        } finally {
            em.close();
        }
    }


    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Coordinates c = em.find(Coordinates.class, id);
            if (c != null) {
                em.remove(c);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Coordinates> findPaged(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Coordinates c", Coordinates.class)
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
