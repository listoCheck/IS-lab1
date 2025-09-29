package src.islab1jee.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import src.islab1jee.entity.Movie;
import src.islab1jee.enums.MovieGenre;

import java.util.List;

@ApplicationScoped
public class MovieRepository {

    @PersistenceContext(unitName = "PostgresPU")
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

    @Transactional
    public void deleteOscarsByGenre(String genre) {
        List<Integer> directors = em.createQuery(
                        "SELECT DISTINCT m.director.id FROM Movie m WHERE m.genre = :genre", Integer.class)
                .setParameter("genre", MovieGenre.valueOf(genre))
                .getResultList();


        if (!directors.isEmpty()) {
            em.createQuery("UPDATE Movie m SET m.oscarsCount = 0 WHERE m.director.id IN :dirs")
                    .setParameter("dirs", directors)
                    .executeUpdate();
        }
    }
}
