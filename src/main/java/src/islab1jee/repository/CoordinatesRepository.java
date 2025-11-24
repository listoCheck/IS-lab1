package src.islab1jee.repository;

import src.islab1jee.model.coordinates.Coordinates;

public class CoordinatesRepository extends GenericRepository<Coordinates> {

    @Override
    protected Class<Coordinates> getEntityClass() {
        return Coordinates.class;
    }

}
