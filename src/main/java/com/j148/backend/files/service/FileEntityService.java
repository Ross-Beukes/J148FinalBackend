/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.files.service;

import com.j148.backend.files.model.FileEntity;
import com.j148.backend.user.model.User;

/**
 *
 * @author yusuf
 */
public interface FileEntityService {
    FileEntity retrieveFileByUserIdAndCategory(User user, FileEntity fileEntity) throws Exception;
}
