package src.islab1jee.models.movie;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ManagedBean("movieRepository")
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
