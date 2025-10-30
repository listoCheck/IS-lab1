package src.islab1jee.controller;

import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.validation.Valid;
import src.islab1jee.model.coordinates.DTO.CoordinatesRequestDto;
import src.islab1jee.model.coordinates.DTO.CoordinatesResponseDto;
import src.islab1jee.service.CoordinatesService;

@Path("/coordinates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CoordinatesController {

    @Inject
    private CoordinatesService service;

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
    @Path("/table")
    public Response getPaged(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("5") int size
    ) {
        List<CoordinatesResponseDto> list = service.getPaged(page, size);
        return Response.ok(list).build();
    }


    @PATCH
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
    @OPTIONS
    @Path("{path: .*}")
    public Response options() {
        return Response.ok().build();
    }
}
