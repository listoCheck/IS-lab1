package src.islab1jee.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import src.islab1jee.model.importobjects.ImportOperation;

import java.util.List;

@ApplicationScoped
public class ImportRepository {

    @PersistenceContext
    private EntityManager em;

    public void save(ImportOperation op) {
        em.persist(op);
    }

    public List<ImportOperation> findAllPaged(int page, int size) {
        return em.createQuery(
                        "SELECT i FROM ImportOperation i ORDER BY i.timestamp DESC",
                        ImportOperation.class
                )
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countAll() {
        return em.createQuery("SELECT COUNT(i) FROM ImportOperation i", Long.class)
                .getSingleResult();
    }
}
