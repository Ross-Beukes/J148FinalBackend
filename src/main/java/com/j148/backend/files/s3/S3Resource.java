package com.j148.backend.files.s3;

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
                               @FormParam("fileSize") long fileSize,
                               @FormParam("fileName") String fileName,
                               @FormParam("contentType") String contentType) {
        try {
            s3Service.uploadFile(fileName, fileStream, fileSize, contentType);
            return Response.ok("File uploaded successfully: " + fileName).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("File upload failed: " + e.getMessage())
                    .build();
        }
    }

    // Download endpoint
    @GET
    @Path("/download/{key}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("key") String key) {
        try {
            InputStream fileStream = s3Service.downloadFile(key);

            StreamingOutput stream = output -> {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                fileStream.close();
            };

            return Response.ok(stream)
                    .header("Content-Disposition", "attachment; filename=\"" + key + "\"")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("File not found: " + key)
                    .build();
        }
    }
}
