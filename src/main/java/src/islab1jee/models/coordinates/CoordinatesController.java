package src.islab1jee.models.coordinates;


import java.util.List;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import src.islab1jee.models.coordinates.dto.*;

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ManagedBean("coordinatesController")
@RequestScoped
public class CoordinatesController {

    @ManagedProperty(value = "#{coordinatesService}")
    private CoordinatesService service;

    public void setService(CoordinatesService service) {
        this.service = service;
    }

    @POST
    public Response create(@Valid CoordinatesRequestDto dto) {
        return Response.ok(service.create(dto)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        return Response.ok(service.getById(id)).build();
    }

    @GET
    public Response getAll() {
        List<CoordinatesResponseDto> list = service.getAll();
        return Response.ok(list).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, @Valid CoordinatesRequestDto dto) {
        return Response.ok(service.update(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
