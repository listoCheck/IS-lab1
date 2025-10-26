package src.islab1jee.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.glassfish.jersey.media.multipart.FormDataParam;
import src.islab1jee.enums.ImportStatus;
import src.islab1jee.model.importobjects.ImportOperation;
import src.islab1jee.repository.ImportRepository;
import src.islab1jee.service.ImportService;

import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Path("/import")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ImportController {

    @Inject
    ImportService importService;

    @Inject
    ImportRepository importRepository;

    @Context
    SecurityContext securityContext;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("file") InputStream fileStream) {
        try {
            if (fileStream == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("message", "Файл не предоставлен"))
                        .build();
            }

            ImportOperation op = importService.processImport(fileStream);

            if (op.getStatus() == ImportStatus.SUCCESS) {
                return Response.ok(Map.of("addedCount", op.getAddedCount())).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("message", op.getErrorMessage()))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("message", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/history")
    public List<ImportOperation> getHistory() {
        Principal user = securityContext.getUserPrincipal();
        boolean isAdmin = securityContext.isUserInRole("ADMIN");

        if (isAdmin) {
            return importRepository.findAll();
        } else {
            return importRepository.findByUser(user.getName());
        }
    }
}
