package org.acme.controller;

import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.dto.ImageUploadFormDTO;
import org.jboss.resteasy.reactive.MultipartForm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Path("/images")
public class ImageController {

    @GET
    @Path("/{imageName}")
    public Response getImage(@PathParam("imageName") String imageName) {
        // Define o diretório externo onde as imagens são armazenadas
        String imageDirectory = "opt/app/images";
        File imageFile = new File(imageDirectory, imageName);

        if (imageFile.exists() && imageFile.isFile()) {
            try {
                byte[] imageData = Files.readAllBytes(imageFile.toPath());
                // Determina o tipo MIME da imagem
                String mimeType = Files.probeContentType(imageFile.toPath());
                return Response.ok(imageData)
                        .type(mimeType != null ? mimeType : "application/octet-stream")
                        .build();
            } catch (Exception e) {
                // Tratar exceções e retornar um erro apropriado
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Error reading image")
                        .build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
