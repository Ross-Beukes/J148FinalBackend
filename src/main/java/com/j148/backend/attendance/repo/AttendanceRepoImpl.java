package com.j148.backend.attendance.repo;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.attendance.model.Attendance.Register;
import com.j148.backend.config.DBConfig;
import com.j148.backend.contractor.model.Contractor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceRepoImpl extends DBConfig implements AttendanceRepo {

    private static final String url = "jdbc:mysql://localhost:3306/hrms?autoReconnect=true&useSSL=false";
    private Timestamp timestamp;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, "root", "root");
    }

    /**
     * Inserts a attendance record into the database
     *
     * @param attendance the attendance object containing details to be saved.
     * @return Optional of attendance if Insertion is successful, or return an
     * Empty Optional if the Insertion was not successful
     */
    @Override
    public Optional<Attendance> createAttendanceRecord(Attendance attendance) throws SQLException {
        String query = "INSERT into attendance (time_in,time_out, register, contractor_id) VALUES (?,?,?,?)";
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
     * @return an Optional containing the Attendance object if found, otherwise
     * an empty Optional
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
     * @return Optional of attendance if the Update is successful, or return an
     * empty Optional if Update was not successful.
     */
    @Override
    public Optional<Attendance> updateAttendance(Attendance attendance) throws SQLException {
        String query = "UPDATE attendance SET timeIn = ?, timeOut = ?, register = ?, contractor_id = ? WHERE attendanceId = ?";
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
        try (Connection con = getConnection(); PreparedStatement statement = con.prepareStatement(query); ResultSet rs = statement.executeQuery()) {
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
    public List<Attendance> todaysAttenance() throws SQLException {
        String query = "SELECT * FROM attendance WHERE time_in = CURDATE()";
        List<Attendance> todaysAttendances = new ArrayList<>();
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contractor contractor = Contractor.builder().contractorId(rs.getLong("contractor_id")).build();
                    Attendance attendance;
                    attendance = Attendance.builder().
                            attendanceId(rs.getLong("attendance_id")).
                            contractor(contractor).
                            timeIn(rs.getTimestamp("time_in").toLocalDateTime()).
                            timeOut(rs.getTimestamp("time_out").toLocalDateTime()).
                            register(Register.valueOf(rs.getString("register"))).
                            build();
                    todaysAttendances.add(attendance);
                }
            }
        }
        return todaysAttendances;
    }

    @Override
    public Optional<Attendance> retreiveAttendanceByContractor(Attendance attendance) throws SQLException {
        String query = "SELECT * FROM attendance WHERE contractor_id = ? AND time_in = CURDATE()";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, attendance.getContractor().getContractorId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Long attendanceID = rs.getLong("attendance_id");
                    Contractor contractor = Contractor.builder().contractorId(rs.getLong("contractor_id")).build();
                    LocalDateTime time_in = rs.getTimestamp("time_in").toLocalDateTime();
                    LocalDateTime time_out = rs.getTimestamp("time_out").toLocalDateTime();
                    Register register = Register.valueOf(rs.getString("register"));
                    Attendance foundAttendance = Attendance.builder().
                            attendanceId(attendanceID).
                            contractor(contractor).
                            timeIn(time_in).
                            timeOut(time_out).
                            register(register).
                            build();
                    return Optional.of(foundAttendance);
                } else {
                    return Optional.empty();
                }
            }
        }

        }

}
