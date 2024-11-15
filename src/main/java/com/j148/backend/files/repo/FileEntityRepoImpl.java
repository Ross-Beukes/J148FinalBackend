package com.j148.backend.files.repo;

import com.j148.backend.Exceptions.FileNotFoundException;
import com.j148.backend.config.DBConfig;
import com.j148.backend.user.model.User;
import jakarta.servlet.http.Part;
import com.j148.backend.files.model.FileEntity;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.nio.file.Path;
import java.nio.file.Paths;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class FileEntityRepoImpl extends DBConfig implements FileEntityRepo {
    private static final Logger LOGGER = Logger.getLogger(FileEntityRepoImpl.class.getName());
    private static final Path UPLOAD_DIR;

    static {
        try {
            // Initialize upload directory in user's home directory
            UPLOAD_DIR = Paths.get(
                    System.getProperty("hrms.upload.dir",
                            Paths.get(System.getProperty("user.home"), "hrms", "uploads").toString()
                    )
            );
            Files.createDirectories(UPLOAD_DIR);
            LOGGER.info("Upload directory initialized at: " + UPLOAD_DIR);
        } catch (IOException e) {
            LOGGER.severe("Failed to create upload directory: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public Optional<FileEntity> saveFile(FileEntity fileEntity) {
        String query = "INSERT INTO files(fileType, category, dateAdded, path, user, verified) Values(?, ?, ?, ?, ?, ?)";

        try (Connection con = getCon();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            con.setAutoCommit(false);
            Savepoint beforeUserInsert = con.setSavepoint();

            ps.setString(1, fileEntity.getFileType());
            ps.setString(2, String.valueOf(fileEntity.getCategory()));
            ps.setTimestamp(3, Timestamp.valueOf(fileEntity.getDateAdded()));
            ps.setString(4, fileEntity.getPath());
            ps.setLong(5, fileEntity.getUser().getUserId());
            ps.setString(6, fileEntity.getVerified().toString());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        fileEntity.setFileId(rs.getLong(1));
                        con.commit();
                        return Optional.of(fileEntity);
                    }
                }
            }

            con.rollback(beforeUserInsert);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving file entity", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<FileEntity> save(Part filePart, User user, FileEntity.Category category)
            throws SQLException {
        String fileName = "";

        try (Connection con = getCon()) {
            con.setAutoCommit(false);
            Savepoint beforeFileSave = con.setSavepoint();

            try {
                // Generate unique filename
                fileName = generateUniqueFileName(filePart);
                Path fullPath = UPLOAD_DIR.resolve(fileName);

                // Save physical file
                savePhysicalFile(filePart, fileName);

                // Save to database using normalized path
                String sql = """
                    INSERT INTO files (user_id, file_type, category, date_added, path, verified) 
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;

                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, user.getUserId());
                    ps.setString(2, filePart.getContentType());
                    ps.setString(3, category.toString());
                    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(5, fullPath.normalize().toString());
                    ps.setString(6, FileEntity.Verified.WAITING.toString());

                    if (ps.executeUpdate() > 0) {
                        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                FileEntity savedFile = FileEntity.builder()
                                        .fileId(generatedKeys.getLong(1))
                                        .user(user)
                                        .fileType(filePart.getContentType())
                                        .category(category)
                                        .dateAdded(LocalDateTime.now())
                                        .path(fullPath.normalize().toString())
                                        .verified(FileEntity.Verified.WAITING)
                                        .build();

                                con.commit();
                                LOGGER.info("File saved successfully: " + fileName);
                                return Optional.of(savedFile);
                            }
                        }
                    }

                    con.rollback(beforeFileSave);
                    return Optional.empty();
                }
            } catch (Exception e) {
                con.rollback(beforeFileSave);
                handleSaveError(fileName, e);
                throw new SQLException("Failed to save file", e);
            }
        }
    }

    @Override
    public Optional<Boolean> deleteFile(FileEntity fileEntity) throws SQLException {
        try (Connection con = getCon()) {
            con.setAutoCommit(false);
            Savepoint beforeFileDelete = con.setSavepoint();

            try {
                // Delete physical file first
                Path filePath = Paths.get(fileEntity.getPath());
                if (!Files.deleteIfExists(filePath)) {
                    LOGGER.warning("Physical file not found: " + fileEntity.getPath());
                }

                // Delete database record
                String sql = "DELETE FROM files WHERE file_id = ?";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setLong(1, fileEntity.getFileId());

                    if (ps.executeUpdate() > 0) {
                        con.commit();
                        LOGGER.info("File deleted successfully: " + fileEntity.getPath());
                        return Optional.of(true);
                    }

                    con.rollback(beforeFileDelete);
                    return Optional.of(false);
                }
            } catch (Exception e) {
                con.rollback(beforeFileDelete);
                LOGGER.log(Level.SEVERE, "Error deleting file", e);
                throw new SQLException("Failed to delete file", e);
            }
        }
    }

    @Override
    public Optional<FileEntity> findById(FileEntity fileEntity) throws SQLException {
        String sql = """
            SELECT f.*, u.* 
            FROM files f
            JOIN user u ON f.user_id = u.user_id
            WHERE f.file_id = ?
            """;

        try (Connection con = getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, fileEntity.getFileId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFileFromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<byte[]> downloadFile(FileEntity file) throws SQLException, IOException {
        Optional<FileEntity> fileRecord = findById(file);
        if (fileRecord.isEmpty()) {
            LOGGER.warning("File record not found in database: " + file.getFileId());
            return Optional.empty();
        }

        Path filePath = Paths.get(fileRecord.get().getPath());
        if (!Files.exists(filePath)) {
            LOGGER.severe("File missing from filesystem: " + filePath);
            return Optional.empty();
        }

        return Optional.of(Files.readAllBytes(filePath));
    }

    private String generateUniqueFileName(Part filePart) {
        return LocalDateTime.now().toString().replace(":", "-")
                + "_" + getSubmittedFileName(filePart);
    }

    private void savePhysicalFile(Part filePart, String fileName) throws IOException {
        Path filePath = UPLOAD_DIR.resolve(fileName);
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void handleSaveError(String fileName, Exception e) {
        try {
            if (!fileName.isEmpty()) {
                Path filePath = UPLOAD_DIR.resolve(fileName);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException deleteError) {
            LOGGER.severe("Failed to delete file after error: " + deleteError.getMessage());
        }
        LOGGER.log(Level.SEVERE, "Error saving file", e);
    }

    private String getSubmittedFileName(Part part) {
        String header = part.getHeader("content-disposition");
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown";
    }

    private FileEntity mapFileFromResultSet(ResultSet rs) throws SQLException {
        return FileEntity.builder()
                .fileId(rs.getLong("file_id"))
                .user(mapUserFromResultSet(rs))
                .fileType(rs.getString("file_type"))
                .category(FileEntity.Category.valueOf(rs.getString("category")))
                .dateAdded(rs.getTimestamp("date_added").toLocalDateTime())
                .path(rs.getString("path"))
                .verified(FileEntity.Verified.valueOf(rs.getString("verified")))
                .build();
    }

    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        return User.builder()
                .userId(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .surname(rs.getString("surname"))
                .email(rs.getString("email"))
                .gender(rs.getString("gender"))
                .idNumber(rs.getString("id_number"))
                .role(User.Role.valueOf(rs.getString("role")))
                .race(rs.getString("race"))
                .location(rs.getString("location"))
                .age(rs.getInt("age"))
                .build();
    }

    @Override
    public List<FileEntity> findById(long fileId) {
        String query = "SELECT * FROM files WHERE fileId = ?";
        List<FileEntity> files = new ArrayList<>();

        try (Connection con = getCon();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setLong(1, fileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    files.add(new FileEntity(
                            rs.getLong(1),
                            rs.getString(2),
                            FileEntity.Category.valueOf(rs.getString(3)),
                            rs.getTimestamp(4).toLocalDateTime(),
                            rs.getString(5),
                            User.builder().userId(rs.getLong(6)).build(),
                            FileEntity.Verified.valueOf(rs.getString(7))
                    ));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding files by ID", ex);
        }
        return files;
    }

    @Override
    public List<FileEntity> findByCategory(FileEntity.Category category) {
        String query = "SELECT * FROM files WHERE category = ?";
        List<FileEntity> files = new ArrayList<>();

        try (Connection con = getCon();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, category.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    files.add(new FileEntity(
                            rs.getLong(1),
                            rs.getString(2),
                            FileEntity.Category.valueOf(rs.getString(3)),
                            rs.getTimestamp(4).toLocalDateTime(),
                            rs.getString(5),
                            User.builder().userId(rs.getLong(6)).build(),
                            FileEntity.Verified.valueOf(rs.getString(7))
                    ));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding files by category", ex);
        }
        return files;
    }

    @Override
    public List<FileEntity> findByStatus(FileEntity.Verified verified) {
        String query = "SELECT * FROM files WHERE verified = ?";
        List<FileEntity> files = new ArrayList<>();

        try (Connection con = getCon();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, verified.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    files.add(new FileEntity(
                            rs.getLong(1),
                            rs.getString(2),
                            FileEntity.Category.valueOf(rs.getString(3)),
                            rs.getTimestamp(4).toLocalDateTime(),
                            rs.getString(5),
                            User.builder().userId(rs.getLong(6)).build(),
                            FileEntity.Verified.valueOf(rs.getString(7))
                    ));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding files by status", ex);
        }
        return files;
    }

    @Override
    public List<FileEntity> getAllFiles() {
        String query = "SELECT * FROM files";
        List<FileEntity> files = new ArrayList<>();

        try (Connection con = getCon();
             PreparedStatement ps = con.prepareStatement(query)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    files.add(new FileEntity(
                            rs.getLong(1),
                            rs.getString(2),
                            FileEntity.Category.valueOf(rs.getString(3)),
                            rs.getTimestamp(4).toLocalDateTime(),
                            rs.getString(5),
                            User.builder().userId(rs.getLong(6)).build(),
                            FileEntity.Verified.valueOf(rs.getString(7))
                    ));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving all files", ex);
        }
        return files;
    }

    @Override
    public Optional<FileEntity> UploadFileS3(FileEntity fileEntity) throws SQLException {

     if(fileEntity != null){
        String accessKey = "";
        String SecretKey =  "";
        String bucketName = "";
        Region region = Region.AF_SOUTH_1;

        //Uploading file to S3 File Storage , needs to be tested
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey,SecretKey );
        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(Paths.get(fileEntity.getPath()).getFileName().toString())
                .build();

        s3Client.putObject(putRequest, Paths.get(fileEntity.getPath()));
        System.out.println("File has been successfully uploaded ");

    try{

    //Add a file record to the Database
    return saveFile(fileEntity);

    }catch(Exception e){
          LOGGER.log(Level.SEVERE, "Error saving uploaded file to Database", e);
    }
     }else {
         throw new FileNotFoundException("Error , it was not possible to find this file");
     }


        return Optional.empty();

    }


    @Override
    public Optional<FileEntity> downloadFileS3(FileEntity fileEntity) throws SQLException {
        if(fileEntity != null){

        String accessKey = "";
        String SecretKey =  "";
        String bucketName = "";
        Region region = Region.AF_SOUTH_1;

        //Find file Entity
        fileEntity = findById(fileEntity).get();

        if(fileEntity.getPath() != null){
        //Download File Entity
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey,SecretKey );
        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

         GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(Path.of(fileEntity.getPath()).getFileName().toString())
                .build();

         //Construct destination path
         String userHome = System.getProperty("user.home");
         String downloadsPath = userHome + "/Downloads/" + Path.of(fileEntity.getPath()).getFileName();

        s3Client.getObject(getRequest, Paths.get(downloadsPath));
        System.out.println("File downloaded successfully!");
        return Optional.of(fileEntity);

        }//If statement end
        else{

        throw new FileNotFoundException("The File Path does not exist, File was not found ");

        }

        }else{
            throw new FileNotFoundException("Sorry, The No such file Exists ");
        }



    }




    @Override
    public Optional<FileEntity> findFileByUserIdAndCategory(User user, FileEntity.Category category) throws SQLException{
        String query = "SELECT * FROM files WHERE category = ? AND user_id = ?";

        try (Connection con = getCon();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, category.toString());
            ps.setLong(2, user.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    FileEntity fileEntity = FileEntity.builder()
                .fileId(rs.getLong(1))
                .user(user)
                .fileType(rs.getString(3))
                .category(FileEntity.Category.valueOf(rs.getString(4)))
                .dateAdded(rs.getTimestamp(5).toLocalDateTime())
                .path(rs.getString(6))
                .verified(FileEntity.Verified.valueOf(rs.getString(7)))
                .build();

                    return Optional.of(fileEntity);
                }

            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error finding file by category and user ID", ex);
        }
        return Optional.empty();
    }
}