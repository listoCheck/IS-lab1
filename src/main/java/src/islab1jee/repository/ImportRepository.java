package src.islab1jee.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import src.islab1jee.model.importobjects.ImportOperation;
import src.islab1jee.utils.DBCPDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ImportRepository {

    private final EntityManagerFactory emf;

    public ImportRepository() {
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

    public ImportOperation save(ImportOperation op) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (op.getId() == null) {
                em.persist(op);
            } else {
                op = em.merge(op);
            }
            em.getTransaction().commit();
            return op;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<ImportOperation> findAllPaged(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT i FROM ImportOperation i ORDER BY i.timestamp DESC",
                            ImportOperation.class)
                    .setFirstResult((page - 1) * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(i) FROM ImportOperation i", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
