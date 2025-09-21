package src.islab1jee.models.coordinates;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import src.islab1jee.models.entity.Coordinates;

import java.util.List;

@ApplicationScoped
public class CoordinatesRepository {

    @PersistenceContext
    private EntityManager em;

    public Coordinates save(Coordinates c) {
        if (c.getId() == null) {
            em.persist(c);
            return c;
        } else {
            return em.merge(c);
        }
    }

    public Coordinates findById(Integer id) {
        return em.find(Coordinates.class, id);
    }

    public List<Coordinates> findAll() {
        return em.createQuery("SELECT c FROM Coordinates c", Coordinates.class)
                .getResultList();
    }

    public void delete(Integer id) {
        Coordinates c = findById(id);
        if (c != null) {
            em.remove(c);
        }
    }
}
