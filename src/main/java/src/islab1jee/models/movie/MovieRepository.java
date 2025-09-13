package src.islab1jee.models.movie;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@ManagedBean(name = "movieRepository")
@RequestScoped
public class MovieRepository {

    @PersistenceContext(unitName = "studsPU")
    private EntityManager em;

    public Movie save(Movie movie) {
        if (movie.getId() == null) {
            em.persist(movie);
            return movie;
        }
        return em.merge(movie);
    }

    public Movie findById(Integer id) {
        return em.find(Movie.class, id);
    }

    public List<Movie> findAll() {
        return em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
    }

    public void delete(Integer id) {
        Movie movie = findById(id);
        if (movie != null) {
            em.remove(movie);
        }
    }
}
