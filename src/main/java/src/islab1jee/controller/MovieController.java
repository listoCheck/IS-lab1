package src.islab1jee.controller;

import java.util.Collections;
import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import src.islab1jee.DTO.CoordinatesResponseDto;
import src.islab1jee.DTO.PersonResponseDto;
import src.islab1jee.enums.MovieGenre;
import src.islab1jee.service.MovieService;
import src.islab1jee.DTO.MovieRequestDto;
import src.islab1jee.DTO.MovieResponseDto;

@Named
@RequestScoped
@Path("/movie")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieController {

    @Inject
    private MovieService service;

    @POST
    public Response create(@Valid MovieRequestDto dto) {
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
        List<MovieResponseDto> list = service.getPaged(page, size);
        return Response.ok(list).build();
    }

    @GET
    @Path("/middle")
    public Response getMiddle(){
        Double middle = service.getMiddle();
        return Response.ok(middle).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Integer id, @Valid MovieRequestDto dto) {
        return Response.ok(service.update(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/countByGenre")
    public Response countByGenre(@QueryParam("genre") MovieGenre genre) {
        if (genre == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Genre is required")
                    .build();
        }
        return Response.ok(service.countByGenre(genre)).build();
    }

    @GET
    @Path("/tagline")
    public Response findByTagline(
            @QueryParam("tagline") int tagline,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("5") int size
    ) {
        List<MovieResponseDto> list = service.getByTagline(page, size, tagline);
        return Response.ok(list).build();
    }

    @GET
    @Path("/oscars")
    public Response findByOscars(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("5") int size
    ) {
        List<MovieResponseDto> list = service.getByOscars(page, size);
        return Response.ok(list).build();
    }

    @PUT
    @Path("/oscars/deleteByGenre")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteOscarsByGenre(@QueryParam("genre") String genre) {
        service.deleteOscarsByGenre(genre);
        return Response.ok().build();
    }

}
