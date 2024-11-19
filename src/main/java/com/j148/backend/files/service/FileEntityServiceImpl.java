/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.files.service;

import com.j148.backend.files.model.FileEntity;
import com.j148.backend.files.repo.FileEntityRepo;
import com.j148.backend.files.repo.FileEntityRepoImpl;
import com.j148.backend.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author yusuf
 */

@ApplicationScoped
public class FileEntityServiceImpl implements FileEntityService {

    @Inject
    private FileEntityRepo fileEntityRepo;

    @Override
    public FileEntity retrieveFileByUserIdAndCategory(User user, FileEntity.Category category) throws Exception {
        if (user != null && category != null) {
            validateUserID(user);
            return fileEntityRepo.findFileByUserIdAndCategory(user, category).orElseThrow(()
                    -> new Exception("There was an error retrieving the file by userID and category"));
        } else if (user == null) {
            throw new NullPointerException("User cannot be null when retrieving a file by userID and category");
        } else {
            throw new NullPointerException("Category cannot be null when retrieving a file by userID and category");
        }
    }

    private void validateUserID(User user) {
        if (user.getUserId() == null) {
            throw new NullPointerException("UserID cannot be null when retrieving file using userID");
        }
        if (user.getUserId() == 0) {
            throw new IllegalArgumentException("UserID returning a 0 when trying to retrieve a specific file");
        }
    }

}
