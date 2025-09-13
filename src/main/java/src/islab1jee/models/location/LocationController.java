package src.islab1jee.models.location;


import java.util.List;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Setter;
import src.islab1jee.models.location.dto.*;

@Setter
@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ManagedBean("locationController")
@RequestScoped
public class LocationController {

    @ManagedProperty(value = "#{locationService}")
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
