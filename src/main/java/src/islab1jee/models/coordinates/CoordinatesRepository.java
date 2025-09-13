package src.islab1jee.models.coordinates;

import src.islab1jee.models.coordinates.Coordinates;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CoordinatesRepository {

    @PersistenceContext(unitName = "studsPU")
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
        return em.createQuery("SELECT c FROM Coordinates c", Coordinates.class).getResultList();
    }

    public void delete(Integer id) {
        Coordinates c = findById(id);
        if (c != null) {
            em.remove(c);
        }
    }
}
