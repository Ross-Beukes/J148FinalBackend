/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.contract_period.repo;

import java.sql.SQLException;

/**
 *
 * @author arshr
 */
public interface ContractorPeriodRepo {
    /**
 * Calculates the average number of enrollments for a specific year.
 *
 * @param year The year for which to calculate the average enrollment.
 * @return The average enrollment count for the specified year.
 * @throws SQLException If a database access error occurs.
 */
double enrollmentAveragesForYear(int year) throws SQLException;

/**
 * Calculates the average number of enrollments over a period of years.
 *
 * @param startYear The starting year of the period.
 * @param endYear The ending year of the period.
 * @return The average enrollment count per year for the specified period.
 * @throws SQLException If a database access error occurs.
 */
double enrollmentAverageForPeriodOfYears(int startYear, int endYear) throws SQLException;

    
    
    
    
    
}
