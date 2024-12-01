package com.j148.backend.files.s3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.files.model.SearchRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.glassfish.jersey.media.multipart.FormDataParam;
import java.util.logging.Logger;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

@RequestScoped
@Path("/files")
public class S3Resource {

    private static final Logger logger = Logger.getLogger(S3Resource.class.getName());

    @Inject
    private S3Service s3Service;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("file") InputStream fileStream,
                               @FormDataParam("metadata") String metadata) {
        try {
            if (fileStream == null) {
                logger.warning("Upload attempt with null file stream");
                return Response.status(Response.Status.BAD_REQUEST).entity("The file is not read").build();
            }
            if (metadata == null || metadata.isEmpty()) {
                logger.warning("Upload attempt with missing metadata");
                return Response.status(Response.Status.BAD_REQUEST).entity("Metadata is missing").build();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            FileEntity fileEntity = objectMapper.readValue(metadata, FileEntity.class);

            if (fileEntity == null) {
                logger.warning("Failed to parse file entity from metadata");
                return Response.status(Response.Status.BAD_REQUEST).entity("The file entity could not be parsed").build();
            }

            logger.info("Uploading file with metadata: " + fileEntity);
            FileEntity returnedFile = s3Service.uploadFile(fileStream, fileEntity);
            return Response.ok(returnedFile).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "File upload failed", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("File upload failed: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(FileEntity fileEntity) {
        try {
            logger.info("Downloading file with ID: " + fileEntity.getFileId());
            InputStream fileStream = s3Service.downloadFile(fileEntity.getFileId().toString());

            StreamingOutput stream = output -> {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                fileStream.close();
            };

            return Response.ok(stream).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "File download failed for ID: " + fileEntity.getFileId(), e);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("File not found: ")
                    .build();
        }
    }

    @GET
    @Path("pending-verifications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingVerifications() {
        try {
            logger.info("Retrieving pending verifications");
            List<FileEntity> pendingFiles = s3Service.findAllPendingVerifications();
            return Response.ok(pendingFiles).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving pending verifications", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve pending verifications")
                    .build();
        }
    }

    @GET
    @Path("contract-period/{periodId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFilesByContractPeriod(@PathParam("periodId") Long periodId) {
        try {
            logger.info("Retrieving files for contract period: " + periodId);
            ContractPeriod period = ContractPeriod.builder()
                    .contractPeriodId(periodId)
                    .build();
            List<FileEntity> files = s3Service.findFilesByContractPeriod(period);
            return Response.ok(files).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid contract period ID: " + periodId);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving files for contract period: " + periodId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve contract period files")
                    .build();
        }
    }

    @GET
    @Path("timesheets/{contractorId}/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApprovedTimesheets(@PathParam("contractorId") Long contractorId,
                                          @PathParam("year") int year) {
        try {
            logger.info("Retrieving approved timesheets for contractor " + contractorId + " year " + year);
            Contractor contractor = Contractor.builder()
                    .contractorId(contractorId)
                    .build();
            List<FileEntity> timesheets = s3Service.getApprovedTimesheetsByYear(contractor, year);
            return Response.ok(timesheets).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid parameters for timesheet retrieval: contractorId=" + contractorId + ", year=" + year);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving approved timesheets for contractor " + contractorId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve approved timesheets")
                    .build();
        }
    }

    @POST
    @Path("valid-files")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValidFiles(SearchRequest request) {
        try {
            logger.info("Searching for valid files with request: " + request);
            List<FileEntity> validFiles = s3Service.findValidFilesByUserIdAndCategory(
                    request.getUser(), request.getFileEntity());
            return Response.ok(validFiles).build();
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid search request parameters: " + request);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving valid files for request: " + request, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to retrieve valid files")
                    .build();
        }
    }
}