package com.j148.backend.files.repo;

import com.j148.backend.files.model.FileEntity;

import java.util.List;
import java.util.Optional;


public interface FileRepo {
    Optional<FileEntity> saveFile(FileEntity file);
    Optional<FileEntity> updateFile(long fileId, FileEntity file);
    List<FileEntity> findById(long fileId);
    List<FileEntity> findByCategory(FileEntity.Category category);
    List<FileEntity> findByStatus(FileEntity.Verified verified);
    List<FileEntity> getAllFiles();
}
