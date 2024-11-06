
package com.j148.backend.attendance.Repo;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.config.DBConfig;
import com.j148.backend.contractor.model.Contractor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceRepoImpl extends DBConfig implements AttendanceRepo {
    private static final String url = "jdbc:mysql://localhost:3306/hrms?autoReconnect=true&useSSL=false";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, "root", "root");
    }

    /**
     * Inserts a attendance record into the database
     *
     * @param attendance the attendance object containing details to be saved.
     * @return Optional of attendance if Insertion is successful, or return an Empty Optional if the Insertion was not successful
     */
    @Override
    public Optional<Attendance> createAttendanceRecord(Attendance attendance) throws SQLException {
        String query = "INSERT into attendance (timeTn,timeOut, register, contractorId) VALUES (?,?,?,?)";
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement(query)) {

            con.setAutoCommit(false);
            statement.setTimestamp(1, Timestamp.valueOf(attendance.getTimeIn()));
            statement.setTimestamp(2, Timestamp.valueOf(attendance.getTimeOut()));
            statement.setString(3, attendance.getRegister().name());
            statement.setLong(4, attendance.getContractor().getContractorId());

            Savepoint save = con.setSavepoint();
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                con.commit();
                return Optional.of(attendance);
            } else {
                con.rollback();
                return Optional.empty();

            }

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
                Attendance attendance = Attendance.builder().build();
                attendance.setAttendanceId(rs.getLong("attendanceId"));
                attendance.setTimeIn(rs.getTimestamp("timeIn").toLocalDateTime());
                attendance.setTimeOut(rs.getTimestamp("timeOut").toLocalDateTime());
                attendance.setRegister(Attendance.Register.valueOf(rs.getString("register")));

                Contractor contractor = Contractor.builder().build();
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
     * @return Optional of attendance if the Update is successful, or return an empty Optional if Update was not successful.
     */

    @Override
    public Optional<Attendance> updateAttendance(Attendance attendance) throws SQLException {
        String query = "UPDATE attendance SET timeIn = ?, timeOut = ?, register = ?, contractorId = ? WHERE attendanceId = ?";
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            statement.setTimestamp(1, Timestamp.valueOf(attendance.getTimeIn()));
            statement.setTimestamp(2, Timestamp.valueOf(attendance.getTimeOut()));
            statement.setString(3, attendance.getRegister().name());
            statement.setLong(4, attendance.getContractor().getContractorId());
            statement.setLong(5, attendance.getAttendanceId());

            Savepoint save = con.setSavepoint();
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                con.commit();
                return Optional.of(attendance);

            }

        }

        return Optional.empty();
    }





    @Override
    public List<Attendance> getAllAttendance() throws SQLException {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance";
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Attendance attendance = Attendance.builder()
                        .attendanceId(rs.getLong("attendanceId")).timeIn(rs.getTimestamp("timeIn").toLocalDateTime())
                                .timeOut(rs.getTimestamp("timeOut").toLocalDateTime())
                                        .register(Attendance.Register.valueOf("register")).build();
                                        Contractor contractor = Contractor.builder().contractorId(rs.getLong("contractorId")).build();
                                        attendance.setContractor(contractor);
                                        attendanceList.add(attendance);

            }


        }
        return attendanceList;
    }
}