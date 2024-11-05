/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.Exceptions;

/**
 *
 * @author Tshireletso
 */
public class LeaveRequestNotFoundException extends Exception{
    
    public LeaveRequestNotFoundException() {}
    
    public LeaveRequestNotFoundException(String message){
        super(message);
    }
    
    public LeaveRequestNotFoundException(String message, Throwable cause){
        super(message,cause);
    }
}
