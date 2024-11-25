package com.j148.backend.files.s3;

import com.j148.backend.files.model.FileEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.InputStream;

@RequestScoped
@Path("/files")
public class S3Resource {

    @Inject
    private S3Service s3Service;

    // Upload endpoint
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormParam("file") InputStream fileStream,
                               FileEntity fileEntity) {
        try {
            s3Service.uploadFile( fileStream, fileEntity);
            return Response.ok("File uploaded successfully: ").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("File upload failed: " + e.getMessage())
                    .build();
        }
    }

    // Download endpoint
    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(com.j148.backend.files.model.FileEntity fileEntity) {
        try {
            InputStream fileStream = s3Service.downloadFile(fileEntity.getFileId().toString());

            StreamingOutput stream = output -> {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                fileStream.close();
            };

            return Response.ok(stream)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("File not found: ")
                    .build();
        }
    }
}
