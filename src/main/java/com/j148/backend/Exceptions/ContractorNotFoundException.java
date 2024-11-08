/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.Exceptions;

/**
 *
 * @author arshr
 */
public class ContractorNotFoundException extends Exception{
    
    public ContractorNotFoundException() {
    }

    public ContractorNotFoundException(String message) {
        super(message);
    }

    public ContractorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
