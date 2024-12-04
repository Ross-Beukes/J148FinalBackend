/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.files.service;

import com.j148.backend.Exceptions.FileNotFoundException;
import com.j148.backend.Exceptions.UserNotFoundException;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.files.repo.FileEntityRepo;
import com.j148.backend.user.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.ArrayList;

import jakarta.transaction.Transactional;

/**
 * @author yusuf
 */

@ApplicationScoped
public class FileEntityServiceImpl implements FileEntityService {

    @Inject
    private FileEntityRepo fileEntityRepo;

    @Override
    public FileEntity retrieveFileByUserIdAndCategory(User user, FileEntity fileEntity) throws Exception {
        if (user != null && fileEntity.getCategory() != null) {
            validateUserID(user);
            return fileEntityRepo.findFileByUserIdAndCategory(user, fileEntity).orElseThrow(()
                    -> new RuntimeException("There was an error retrieving the file by userID and category"));
        } else if (user == null) {
            throw new NullPointerException("User cannot be null when retrieving a file by userID and category");
        } else {
            throw new NullPointerException("Category cannot be null when retrieving a file by userID and category");
        }
    }

    @Override
    @Transactional(rollbackOn = {Exception.class, RuntimeException.class, SQLException.class
    })
    public FileEntity fileVerification(FileEntity fileEntity) throws Exception {
        if (fileEntity.getVerified() == null) {
            throw new RuntimeException("File could not be verified.");
        }

        return fileEntityRepo.fileVerification(fileEntity)
                .orElseThrow(() -> new RuntimeException("File not found."));
    }

    private void validateUserID(User user) {
        if (user.getUserId() == null) {
            throw new NullPointerException("UserID cannot be null when retrieving file using userID");
        }
        if (user.getUserId() == 0) {
            throw new IllegalArgumentException("UserID returning a 0 when trying to retrieve a specific file");
        }
    }

    @Override
    public ArrayList<FileEntity> retrieveFilesWithUsers() throws SQLException, FileNotFoundException, UserNotFoundException {
        ArrayList<FileEntity> usersAndFiles = fileEntityRepo.retrieveFilesWithUsers();

        for (FileEntity file : usersAndFiles) {
            if (file == null || file.getFileId() == 0) {
                throw new FileNotFoundException("There was an error retrieving the file");
            }
            if (file.getUser() == null || file.getUser().getUserId() == 0) {
                throw new UserNotFoundException();
            }
        }

        return usersAndFiles;

    }

}
