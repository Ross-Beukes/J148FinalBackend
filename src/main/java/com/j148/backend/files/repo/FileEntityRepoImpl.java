package com.j148.backend.files.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.j148.backend.files.model.FileEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class FileEntityRepoImpl implements FileEntityRepo {
    private static final Logger LOGGER = Logger.getLogger(FileEntityRepoImpl.class.getName());

    @Inject
    private DBConfig DBConfig;

    @Override
    public Optional<FileEntity> saveFile(FileEntity fileEntity) throws SQLException {
        String query = " INSERT INTO files (user_id, file_type, file_size, category, date_added, verified) VALUES (?, ?, ?, ?, ?, ?) ";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, fileEntity.getUser().getUserId());
            ps.setString(2, fileEntity.getFileType());
            ps.setInt(3, fileEntity.getFileSize());
            ps.setString(4, String.valueOf(fileEntity.getCategory()));
            ps.setTimestamp(5, Timestamp.valueOf(fileEntity.getDateAdded()));
            ps.setString(6, String.valueOf(fileEntity.getVerified()));

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        fileEntity.setFileId(rs.getLong(1));
                        return Optional.of(fileEntity);
                    }
                }
            }

        }
        return Optional.empty();
    }


    @Override
    public Optional<FileEntity> findById(FileEntity fileEntity) throws SQLException {
        String sql = """
                SELECT f.*, u.* 
                FROM files f
                JOIN user u ON f.user_id = u.user_id
                WHERE f.file_id = ?
                """;

        try (Connection con = DBConfig.getCon();
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
    public List<FileEntity> findAllPendingVerifications() throws SQLException {
        // Initialize result list
        List<FileEntity> pendingFiles = new ArrayList<>();

        // SQL query with JOIN to get file and user information
        String query = """
        SELECT f.*, u.* 
        FROM files f
        JOIN user u ON f.user_id = u.user_id
        WHERE f.verified = ?
        """;

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(query)) {

            // Set the WAITING status in the query
            ps.setString(1, FileEntity.Verified.WAITING.toString());

            // Execute query and process results
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Map the user information
                    User user = User.builder()
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

                    // Map the file information
                    FileEntity file = FileEntity.builder()
                            .fileId(rs.getLong("file_id"))
                            .fileType(rs.getString("file_type"))
                            .fileSize(rs.getInt("file_size"))
                            .category(FileEntity.Category.valueOf(rs.getString("category")))
                            .dateAdded(rs.getTimestamp("date_added").toLocalDateTime())
                            .verified(FileEntity.Verified.valueOf(rs.getString("verified")))
                            .user(user)  // Set the mapped user object
                            .build();

                    pendingFiles.add(file);
                }
            }
        }

        return pendingFiles;
}

    @Override
    public List<FileEntity> findFilesByContractPeriod(ContractPeriod contractPeriod) throws SQLException {
        String query = """
            SELECT f.*, u.* 
            FROM files f
            JOIN user u ON f.user_id = u.user_id
            JOIN contractor c ON u.user_id = c.user_id
            WHERE c.contractor_period_id = ?
            ORDER BY f.date_added DESC
        """;

        List<FileEntity> periodFiles = new ArrayList<>();

        try (Connection conn = DBConfig.getCon();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, contractPeriod.getContractPeriodId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Create User object from result set
                    User user = User.builder()
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

                    // Create FileEntity object from result set
                    FileEntity file = FileEntity.builder()
                            .fileId(rs.getLong("file_id"))
                            .fileType(rs.getString("file_type"))
                            .fileSize(rs.getInt("file_size"))
                            .category(FileEntity.Category.valueOf(rs.getString("category")))
                            .dateAdded(rs.getTimestamp("date_added").toLocalDateTime())
                            .user(user)
                            .verified(FileEntity.Verified.valueOf(rs.getString("verified")))
                            .build();

                    periodFiles.add(file);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving files for contract period");
            throw e;
        }

        return periodFiles;
    }

    @Override
    public List<FileEntity> getApprovedTimesheetsByYear(Contractor contractor, int year) throws SQLException {
        List<FileEntity> approvedTimesheets = new ArrayList<>();

        String query = """
        SELECT f.*, u.* 
        FROM files f
        JOIN user u ON f.user_id = u.user_id
        WHERE f.category = ? 
        AND f.verified = ?
        AND u.user_id = ?
        AND YEAR(f.date_added) = ?
        ORDER BY f.date_added DESC
        """;

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, FileEntity.Category.TIMESHEET.toString());
            ps.setString(2, FileEntity.Verified.APPROVED.toString());
            ps.setLong(3, contractor.getUser().getUserId());
            ps.setInt(4, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = User.builder()
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

                    FileEntity file = FileEntity.builder()
                            .fileId(rs.getLong("file_id"))
                            .fileType(rs.getString("file_type"))
                            .fileSize(rs.getInt("file_size"))
                            .category(FileEntity.Category.valueOf(rs.getString("category")))
                            .dateAdded(rs.getTimestamp("date_added").toLocalDateTime())
                            .verified(FileEntity.Verified.valueOf(rs.getString("verified")))
                            .user(user)
                            .build();

                    approvedTimesheets.add(file);
                }
            }
        }

        return approvedTimesheets;
    }
    @Override
    public List<FileEntity> findValidFilesByUserIdAndCategory(User user, FileEntity fileEntity) throws SQLException {
        String query = """
            SELECT * FROM files 
            WHERE user_id = ? 
            AND verified != 'REJECTED'
            ORDER BY date_added DESC
        """;

        List<FileEntity> validFiles = new ArrayList<>();

        try (Connection conn = DBConfig.getCon();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setLong(1, user.getUserId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FileEntity file = FileEntity.builder()
                            .fileId(rs.getLong("file_id"))
                            .fileType(rs.getString("file_type"))
                            .fileSize(rs.getInt("file_size"))
                            .category(FileEntity.Category.valueOf(rs.getString("category")))
                            .dateAdded(rs.getTimestamp("date_added").toLocalDateTime())
                            .user(user)
                            .verified(FileEntity.Verified.valueOf(rs.getString("verified")))
                            .build();

                    validFiles.add(file);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving the files");
            throw e;
        }

        return validFiles;
    }


    private FileEntity mapFileFromResultSet(ResultSet rs) throws SQLException {
        return FileEntity.builder()
                .fileId(rs.getLong("file_id"))
                .user(mapUserFromResultSet(rs))
                .fileType(rs.getString("file_type"))
                .fileSize(rs.getInt("file_size"))
                .category(FileEntity.Category.valueOf(rs.getString("category")))
                .dateAdded(rs.getTimestamp("date_added").toLocalDateTime())
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
    public Optional<FileEntity> findFileByUserIdAndCategory(User user, FileEntity fileEntity) throws SQLException {
        String query = "SELECT * FROM files WHERE category = ? AND user_id = ?";

        try (Connection con = DBConfig.getCon();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, fileEntity.getCategory().toString());
            ps.setLong(2, user.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FileEntity returnedFile = FileEntity.builder()
                            .fileId(rs.getLong(1))
                            .user(user)
                            .fileType(rs.getString(3))
                            .fileSize(rs.getInt(4))
                            .category(FileEntity.Category.valueOf(rs.getString(5)))
                            .dateAdded(rs.getTimestamp(6).toLocalDateTime())
                            .verified(FileEntity.Verified.valueOf(rs.getString(7)))
                            .build();

                    return Optional.of(returnedFile);
                }

            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<FileEntity> fileVerification(FileEntity fileEntity) throws SQLException {
        String query = "UPDATE files SET verified = ? WHERE file_id = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, fileEntity.getVerified().name());
            ps.setLong(2, fileEntity.getFileId());
            if (ps.executeUpdate() > 0) {
                return Optional.of(fileEntity);
            }
        }
        return Optional.empty();
    }

}