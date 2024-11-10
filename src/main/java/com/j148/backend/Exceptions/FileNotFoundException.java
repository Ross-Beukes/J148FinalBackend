/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.Exceptions;

/**
 *
 * @author Tshireletso
 */
public class FileNotFoundException extends RuntimeException {
    
   public FileNotFoundException(){}
   
   public FileNotFoundException(String message){
       super(message);
   }
   
   public FileNotFoundException(String message,Throwable cause){
       super(message,cause);
   }
   
   
   
   
   
   
    
    
    
    
}
