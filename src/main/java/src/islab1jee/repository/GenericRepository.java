package src.islab1jee.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import src.islab1jee.utils.DBCPDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenericRepository<T> {

    private static final EntityManagerFactory emf;

    static {
        DataSource ds = DBCPDataSource.getDataSource();
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.nonJtaDataSource", ds);
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.cache.use_second_level_cache", "true");
        props.put("hibernate.cache.use_query_cache", "true");
        props.put("jakarta.persistence.schema-generation.database.action", "update");

        emf = jakarta.persistence.Persistence.createEntityManagerFactory("PostgresPU", props);
    }

    protected abstract Class<T> getEntityClass();

    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public T save(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (em.contains(entity)) {
                entity = em.merge(entity);
            } else {
                em.persist(entity);
            }
            em.getTransaction().commit();
            return entity;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public T findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(getEntityClass(), id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + getEntityClass().getSimpleName() + " e", getEntityClass())
                    .setHint("org.hibernate.cacheable", true)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<T> findPaged(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + getEntityClass().getSimpleName() + " e", getEntityClass())
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .setHint("org.hibernate.cacheable", true)
                    .getResultList();
        } finally {
            em.close();
        }
    }


    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(getEntityClass(), id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public static void close() {
        emf.close();
    }

}
