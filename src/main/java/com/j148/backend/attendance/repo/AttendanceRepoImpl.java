
package com.j148.backend.attendance.repo;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.config.DBConfig;
import com.j148.backend.contractor.model.Contractor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceRepoImpl extends DBConfig implements AttendanceRepo {
//    private static final String url = "jdbc:mysql://localhost:3306/hrms?autoReconnect=true&useSSL=false";
//
//    private Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(url, "root", "root");
//    }

    /**
     * Inserts a attendance record into the database
     *
     * @param attendance the attendance object containing details to be saved.
     * @return Optional of attendance if Insertion is successful, or return an Empty Optional if the Insertion was not successful
     */
    @Override
    public Optional<Attendance> createAttendanceRecord(Attendance attendance) throws SQLException {
        String query = "INSERT into attendance (time_in,time_out, register, contractor_id) VALUES (?,?,?,?)";
        try (Connection con = getCon(); PreparedStatement statement = con.prepareStatement(query)) {

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
                con.rollback(save);
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
        String query = "SELECT * FROM attendance WHERE attendance_id= ?";
        try (Connection con = getCon(); PreparedStatement statement = con.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Attendance attendance = Attendance.builder().build();
                attendance.setAttendanceId(rs.getLong("attendance_id"));
                attendance.setTimeIn(rs.getTimestamp("time_in").toLocalDateTime());
                attendance.setTimeOut(rs.getTimestamp("time_out").toLocalDateTime());
                attendance.setRegister(Attendance.Register.valueOf(rs.getString("register")));

                Contractor contractor = Contractor.builder().build();
                contractor.setContractorId(rs.getLong("contractor_id"));
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
        String query = "UPDATE attendance SET time_in = ?, time_out = ?, register = ?, contractor_id = ? WHERE attendance_id = ?";
        try (Connection con = getCon(); PreparedStatement statement = con.prepareStatement(query)) {
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

            }else{
                con.rollback(save);
                return Optional.empty();
            }

        }


    }





    @Override
    public List<Attendance> getAllAttendance() throws SQLException {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance";
        try (Connection con = getCon(); PreparedStatement statement = con.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Attendance attendance = Attendance.builder()
                        .attendanceId(rs.getLong("attendance_id")).timeIn(rs.getTimestamp("time_in").toLocalDateTime())
                                .timeOut(rs.getTimestamp("time_out").toLocalDateTime())
                                        .register(Attendance.Register.valueOf("register")).build();
                                        Contractor contractor = Contractor.builder().contractorId(rs.getLong("contractor_id")).build();
                                        attendance.setContractor(contractor);
                                        attendanceList.add(attendance);

            }


        }
        return attendanceList;
    }

    @Override
    public List<Attendance> FindAllAttendanceForContractor(Contractor contractor) throws SQLException {
        List<Attendance>attendanceList = new ArrayList<>();
        String query= "SELECT * FROM attendance WHERE contractor_id = ?";
        try(Connection con = getCon();
        PreparedStatement statement= con.prepareStatement(query)){
            statement.setLong(1, contractor.getContractorId());
            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                Attendance attendance =Attendance.builder().build();
                attendance.setAttendanceId(rs.getLong("attendance_id"));
                attendance.setTimeIn(rs.getTimestamp("time_in").toLocalDateTime());
                attendance.setTimeOut(rs.getTimestamp("time_out").toLocalDateTime());
                attendance.setRegister(Attendance.Register.valueOf(rs.getString("register")));

                attendance.setContractor(contractor);
                attendanceList.add(attendance);
            }
        }
        return attendanceList;
    }
}