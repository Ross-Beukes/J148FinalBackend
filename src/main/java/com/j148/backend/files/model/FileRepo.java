package com.j148.backend.files.model;

import java.util.List;
import java.util.Optional;

public interface FileRepo {
    Optional<Files> saveFile(Files file);
    Optional<Files> updateFile(long fileId, Files file);
    List<Files> findById(long fileId);
    List<Files> findByCategory(Files.Category category);
    List<Files> findByStatus(Files.Verified verified);
    List<Files> getAllFiles();
}
