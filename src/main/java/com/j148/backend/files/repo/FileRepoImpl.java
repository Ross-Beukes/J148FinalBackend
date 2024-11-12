package com.j148.backend.files.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.user.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileRepoImpl extends DBConfig implements FileRepo {

    @Override
    public Optional<FileEntity> saveFile(FileEntity file) {
        String query = "INSERT INTO files(fileType, category, dateAdded, path, user, verified) Values(?, ?, ?, ?, ?, ?)";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            con.setAutoCommit(false);
            ps.setString(1, file.getFileType());
            ps.setString(2, file.getCategory().toString());
            ps.setTimestamp(3, Timestamp.valueOf(file.getDateAdded()));
            ps.setString(4, file.getPath());
            ps.setLong(5, file.getUser().getUserId());
            ps.setString(6, file.getVerified().toString());


            Savepoint beforeUserInsert = con.setSavepoint();
            if (ps.executeUpdate() > 0) {
                con.commit();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        file.setFileId(rs.getLong(1));
                    }
                }
                return Optional.of(file);
            } else {
                con.rollback(beforeUserInsert);
            }
        } catch (SQLException e) {
            Logger.getLogger(FileRepoImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<FileEntity> updateFile(long fileId, FileEntity file) {
        String query = "UPDATE files SET fileType = ?, category = ?, dateAdded = ?, path = ?, user = ?, verified = ? WHERE fileId = ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            con.setAutoCommit(false);

            ps.setString(1, file.getFileType());
            ps.setString(2, file.getCategory().toString());
            ps.setTimestamp(3, Timestamp.valueOf(file.getDateAdded()));
            ps.setString(4, file.getPath());
            ps.setLong(5, file.getUser().getUserId());
            ps.setString(6, file.getVerified().toString());
            ps.setLong(7, fileId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                con.commit();
                file.setFileId(fileId);
                return Optional.of(file);
            } else {
                con.rollback();
            }
        } catch (SQLException ex) {
            Logger.getLogger(FileRepoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Optional.empty();
    }

    @Override
    public List<FileEntity> findById(long fileId) {
        String query = "SELECT * FROM files WHERE fileId = ?";
        List<FileEntity> files = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
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
            Logger.getLogger(FileRepoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return files;
    }

    @Override
    public List<FileEntity> findByCategory(FileEntity.Category category) {
        String query = "SELECT * FROM files WHERE category = ?";
        List<FileEntity> files = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
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
            Logger.getLogger(FileRepoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return files;
    }

    @Override
    public List<FileEntity> findByStatus(FileEntity.Verified verified) {
        String query = "SELECT * FROM files WHERE verified = ?";
        List<FileEntity> files = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
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
            Logger.getLogger(FileRepoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return files;
    }

    @Override
    public List<FileEntity> getAllFiles() {
        String query = "SELECT * FROM files";
        List<FileEntity> files = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
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
            Logger.getLogger(FileRepoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return files;
    }
}
