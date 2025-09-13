package src.islab1jee.models.location;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ManagedBean("locationRepository")
@RequestScoped
public class LocationRepository {

    @PersistenceContext(unitName = "studsPU")
    private EntityManager em;

    public Location save(Location l) {
        if (l.getId() == null) {
            em.persist(l);
            return l;
        } else {
            return em.merge(l);
        }
    }

    public Location findById(Integer id) {
        return em.find(Location.class, id);
    }

    public List<Location> findAll() {
        return em.createQuery("SELECT l FROM Location l", Location.class).getResultList();
    }

    public void delete(Integer id) {
        Location l = findById(id);
        if (l != null) {
            em.remove(l);
        }
    }
}
