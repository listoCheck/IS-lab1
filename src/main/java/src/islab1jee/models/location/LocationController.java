package src.islab1jee.models.location;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import src.islab1jee.models.location.dto.*;

@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class LocationController {

    @Inject
    private LocationService service;

    @POST
    public Response create(@Valid LocationRequestDto dto) {
        return Response.ok(service.create(dto)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        return Response.ok(service.getById(id)).build();
    }

    @GET
    public Response getAll() {
        List<LocationResponseDto> list = service.getAll();
        return Response.ok(list).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, @Valid LocationRequestDto dto) {
        return Response.ok(service.update(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
