/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.applicant_overview.model;

import com.j148.backend.files.model.FileEntity;
import com.j148.backend.user.model.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

/**
 *
 * @author Yusuf
 */
public class ApplicantDocuments {
    private User user;
    private FileEntity idFile;
    private FileEntity matricFile;
    private FileEntity contractFile;
}
