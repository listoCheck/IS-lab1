package src.islab1jee.repository;

import jakarta.enterprise.context.ApplicationScoped;
import src.islab1jee.model.importobjects.ImportOperation;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class ImportRepository extends GenericRepository<ImportOperation> {

    @Override
    protected Class<ImportOperation> getEntityClass() {
        return ImportOperation.class;
    }

    public List<ImportOperation> findAllPaged(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT i FROM ImportOperation i ORDER BY i.timestamp DESC", ImportOperation.class)
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
