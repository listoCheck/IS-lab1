package src.islab1jee.repository;

import jakarta.enterprise.context.ApplicationScoped;
import src.islab1jee.model.location.Location;

@ApplicationScoped
public class LocationRepository extends GenericRepository<Location> {

    @Override
    protected Class<Location> getEntityClass() {
        return Location.class;
    }

}
