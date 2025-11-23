package src.islab1jee.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import src.islab1jee.enums.MovieGenre;
import src.islab1jee.model.movie.Movie;
import src.islab1jee.utils.DBCPDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class MovieRepository {

    private final EntityManagerFactory emf;

    public MovieRepository() {
        DataSource ds = DBCPDataSource.getDataSource();
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.nonJtaDataSource", ds);
        props.put("hibernate.show_sql", true);
        props.put("hibernate.format_sql", true);
        props.put("jakarta.persistence.schema-generation.database.action", "update");

        emf = jakarta.persistence.Persistence.createEntityManagerFactory("PostgresPU", props);
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public Movie save(Movie movie) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            if (movie.getId() == null) {
                em.persist(movie);
            } else {
                movie = em.merge(movie);
            }
            em.getTransaction().commit();
            return movie;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Movie findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movie.class, id);
        } finally {
            em.close();
        }
    }

    public List<Movie> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Movie movie = em.find(Movie.class, id);
            if (movie != null) {
                em.remove(movie);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
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

    public List<Movie> findPaged(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT m FROM Movie m", Movie.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void close() {
        emf.close();
    }
}
