package src.islab1jee.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import src.islab1jee.enums.ImportStatus;
import src.islab1jee.model.importobjects.ImportOperation;
import src.islab1jee.service.ImportService;
import src.islab1jee.repository.ImportRepository;

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
    public Response uploadFile(MultipartFormDataInput input) {
        try {
            InputPart filePart = input.getFormDataMap().get("file").get(0);
            InputStream fileStream = filePart.getBody(InputStream.class, null);

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
