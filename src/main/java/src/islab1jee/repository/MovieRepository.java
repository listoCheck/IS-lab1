package src.islab1jee.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import src.islab1jee.enums.MovieGenre;
import src.islab1jee.model.movie.Movie;

import java.util.List;

@ApplicationScoped
public class MovieRepository extends GenericRepository<Movie> {


    @Override
    protected Class<Movie> getEntityClass() {
        return Movie.class;
    }

    public void deleteOscarsByGenre(String genre) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            List<Integer> directors = em.createQuery(
                            "SELECT DISTINCT m.director.id FROM Movie m WHERE m.genre = :genre", Integer.class)
                    .setParameter("genre", MovieGenre.valueOf(genre))
                    .getResultList();

            if (!directors.isEmpty()) {
                em.createQuery("UPDATE Movie m SET m.oscarsCount = 0 WHERE m.director.id IN :dirs")
                        .setParameter("dirs", directors)
                        .executeUpdate();
            }

            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }


}
