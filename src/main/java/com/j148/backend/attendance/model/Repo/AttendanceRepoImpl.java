
package com.j148.backend.attendance.model.Repo;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.config.DBConfig;
import com.j148.backend.contractor.model.Contractor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class AttendanceRepoImpl extends DBConfig implements AttendanceRepo {
    private static final String url = "jdbc:mysql://localhost:3306/hrms?autoReconnect=true&useSSL=false";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, "root", "root");
    }

    /**
     * Inserts a attendance record into the database
     *
     * @param attendance the attendance object containing details to be saved.
     * @return true if the record was successfully inserted, false if otherwise
     */
    @Override
    public boolean createAttendanceRecord(Attendance attendance) throws SQLException {
        String query = "INSERT into attendance (timeTn,timeOut, register, contractorId) VALUES (?,?,?,?)";
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement(query)) {
            statement.setTimestamp(1, Timestamp.valueOf(attendance.getTimeIn()));
            statement.setTimestamp(2, Timestamp.valueOf(attendance.getTimeOut()));
            statement.setString(3, attendance.getRegister().name());
            statement.setLong(4, attendance.getContractor().getContractorId());

            Savepoint save = con.setSavepoint();
            int affectedRows = statement.executeUpdate();

            if (affectedRows < 0) {
                return false;
            }
            return affectedRows > 0;


        }

    }

    /**
     * Retrieves an attendance record by its ID.
     *
     * @param id the unique identifier of the attendance record.
     * @return an Optional containing the Attendance object if found, otherwise an empty Optional
     */
    @Override
    public Optional<Attendance> getAttendanceByID(Long id) throws SQLException {
        String query = "SELECT * FROM attendance WHERE attendanceId = ?";
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(rs.getLong("attendanceId"));
                attendance.setTimeIn(rs.getTimestamp("timeIn").toLocalDateTime());
                attendance.setTimeOut(rs.getTimestamp("timeOut").toLocalDateTime());
                attendance.setRegister(Attendance.Register.valueOf(rs.getString("register")));

                Contractor contractor = new Contractor();
                contractor.setContractorId(rs.getLong("contractorId"));
                attendance.setContractor(contractor);

                return Optional.of(attendance);
            } else {
                return Optional.empty();

            }
        }
    }

    /**
     * Updates an existing attendance record in the database
     *
     * @param attendance the Attendance object containing updated details.
     * @return true if the record was successfully updated, false otherwise.
     */

    @Override
    public boolean updateAttendance(Attendance attendance) throws SQLException {
        String query = "UPDATE attendance SET timeIn = ?, timeOut = ?, register = ?, contractorId = ? WHERE attendanceId = ?";
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement(query)) {
            statement.setTimestamp(1, Timestamp.valueOf(attendance.getTimeIn()));
            statement.setTimestamp(2, Timestamp.valueOf(attendance.getTimeOut()));
            statement.setString(3, attendance.getRegister().name());
            statement.setLong(4, attendance.getContractor().getContractorId());
            statement.setLong(5, attendance.getAttendanceId());

            Savepoint save = con.setSavepoint();
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an attendance record from the database by its id.
     *
     * @param id the unique identifier of attendance record to delete
     * @return true if the record was successfully deleted, false if otherwise
     */




    @Override
    public List<Attendance> getAllAttendance() throws SQLException {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance";
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Attendance attendance = new Attendance();
                attendance.setAttendanceId(rs.getLong("attendanceId"));
                attendance.setTimeIn(rs.getTimestamp("timeIn").toLocalDateTime());
                attendance.setTimeOut(rs.getTimestamp("timeOut").toLocalDateTime());
                attendance.setRegister(Attendance.Register.valueOf(rs.getString("register")));

                Contractor contractor = new Contractor();
                contractor.setContractorId(rs.getLong("contractorId"));
                attendance.setContractor(contractor);

                attendanceList.add(attendance);
            }


        }
        return attendanceList;
    }
}