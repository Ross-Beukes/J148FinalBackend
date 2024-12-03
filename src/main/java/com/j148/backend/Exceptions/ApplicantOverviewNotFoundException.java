/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.Exceptions;

/**
 *
 * @author User
 */
public class ApplicantOverviewNotFoundException extends RuntimeException{

    public ApplicantOverviewNotFoundException() {
    }
    
    public ApplicantOverviewNotFoundException(String message) {
        super(message);
    }

    public ApplicantOverviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
