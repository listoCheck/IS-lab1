package src.islab1jee.models.movie;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.validation.*;
import javax.ws.rs.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.*;

import java.util.List;
import src.islab1jee.models.movie.dto.MovieRequestDto;
import src.islab1jee.models.movie.dto.MovieResponseDto;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ManagedBean(name = "movieController")
@RequestScoped
public class MovieController {

    @ManagedProperty(value = "#{movieService}")
    private MovieService service;

    public void setService(MovieService service) {
        this.service = service;
    }

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
    public Response getAll() {
        List<MovieResponseDto> movies = service.getAll();
        return Response.ok(movies).build();
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
}
