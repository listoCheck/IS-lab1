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

    public List<ImportOperation> findAll() {
        return em.createQuery(
                "SELECT i FROM ImportOperation i ORDER BY i.timestamp DESC",
                ImportOperation.class
        ).getResultList();
    }

    public List<ImportOperation> findByUser(String username) {
        return em.createQuery(
                        "SELECT i FROM ImportOperation i WHERE i.username = :u ORDER BY i.timestamp DESC",
                        ImportOperation.class
                ).setParameter("u", username)
                .getResultList();
    }
}
