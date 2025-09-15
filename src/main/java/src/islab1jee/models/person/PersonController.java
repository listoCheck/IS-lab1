package src.islab1jee.models.person;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import src.islab1jee.models.person.dto.PersonRequestDto;
import src.islab1jee.models.person.dto.PersonResponseDto;

@Named
@RequestScoped
@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonController {

    @Inject
    private PersonService service;

    @POST
    public Response create(@Valid PersonRequestDto dto) {
        return Response.ok(service.create(dto)).build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Integer id) {
        return Response.ok(service.getById(id)).build();
    }

    @GET
    public Response getAll() {
        List<PersonResponseDto> list = service.getAll();
        return Response.ok(list).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, @Valid PersonRequestDto dto) {
        return Response.ok(service.update(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
