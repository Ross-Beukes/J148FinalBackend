package com.j148.backend.files.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.j148.backend.files.model.FileEntity;

import java.sql.*;
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
        String query = "UPDATE files SET verified = ? WHERE file_id = ? AND verified = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, fileEntity.getVerified().name());
            ps.setLong(2, fileEntity.getFileId());
            ps.setString(3, "WAITING");
            if (ps.executeUpdate() > 0) {
                return Optional.of(fileEntity);
            }
        }
        return Optional.empty();
    }

}