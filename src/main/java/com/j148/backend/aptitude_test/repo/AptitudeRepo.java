/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.aptitude_test.repo;

/**
 *
 * @author MIANTSUMI
 */
import com.j148.backend.aptitude_test.model.AptitudeTest;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AptitudeRepo {
    
     /**
     * Saves a new AptitudeTest to the database.
     * 
     * @param aptitudeTest the AptitudeTest entity to save
     * @return the saved AptitudeTest entity, with any generated fields populated
     */
    AptitudeTest create (AptitudeTest aptitudeTest) throws SQLException;
    
     
    /**
     * Retrieves an AptitudeTest from the database by its ID.
     * 
     * @param id the ID of the AptitudeTest to retrieve
     * @return an Optional containing the found AptitudeTest, or empty if not found
     */
    
    Optional<AptitudeTest> findById(Long id) throws SQLException;
    
     /**
     * Retrieves all AptitudeTest entities from the database.
     * 
     * @return a list of all AptitudeTest entities
     */
    
    List<AptitudeTest> findAll() throws SQLException;
      /**
     * Updates an existing AptitudeTest in the database.
     * 
     * @param aptitudeTest the AptitudeTest entity with updated values
     * @return the updated AptitudeTest entity
     */
    
    
    AptitudeTest update(AptitudeTest aptitudeTest) throws SQLException;
    
    
    /**
     * Deletes an AptitudeTest from the database by its ID.
     * 
     * @param id the ID of the AptitudeTest to delete
     */
    
    void deleteById(Long id) throws SQLException;
}