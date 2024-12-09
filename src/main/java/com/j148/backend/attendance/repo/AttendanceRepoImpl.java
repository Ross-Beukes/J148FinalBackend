package com.j148.backend.attendance.repo;

import com.j148.backend.attendance.model.Attendance;
import com.j148.backend.user.model.User;
import com.j148.backend.attendance.model.Attendance.Register;
import com.j148.backend.config.DBConfig;
import com.j148.backend.contract_period.model.ContractPeriod;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.contractor.model.Contractor.Status;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AttendanceRepoImpl implements AttendanceRepo {

    /**
     * Inserts an attendance record into the database
     *
     * @param attendance the attendance object containing details to be saved.
     * @return Optional of attendance if Insertion is successful, or return an
     * Empty Optional if the Insertion was not successful
     */
    @Inject
    private DBConfig DBConfig;
    
    @Override
    public Optional<Attendance> createAttendanceRecord(Attendance attendance) throws SQLException {
        String query = "INSERT into attendance (time_in, register, contractor_id,time_out) VALUES (?,?,?,?)";
        try (Connection con = DBConfig.getCon(); PreparedStatement statement = con.prepareStatement(query)) {
            statement.setTimestamp(1, Timestamp.valueOf(attendance.getTimeIn()));
            statement.setString(2, attendance.getRegister().name());
            statement.setLong(3, attendance.getContractor().getContractorId());
            statement.setTimestamp(4, Timestamp.valueOf(attendance.getTimeOut()));
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                return Optional.of(attendance);
            } else {
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
        String query = "SELECT * FROM attendance WHERE attendance_id = ?";
        
        try (Connection con = DBConfig.getCon(); PreparedStatement statement = con.prepareStatement(query)) {
            
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Attendance attendance = Attendance.builder().build();
                attendance.setAttendanceId(rs.getLong("attendance_id"));
                attendance.setTimeIn(rs.getTimestamp("time_in").toLocalDateTime());
                if (rs.getTimestamp("time_out") != null) {
                    attendance.setTimeOut(rs.getTimestamp("time_out").toLocalDateTime());
                }
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
     * @return Optional of attendance if the Update is successful, or return an
     * empty Optional if Update was not successful.
     */
    @Override
    public Optional<Attendance> updateAttendance(Attendance attendance) throws SQLException {
        String query = "UPDATE attendance SET time_in = ?, time_out = ?, register = ?, contractor_id = ? WHERE attendance_id = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement statement = con.prepareStatement(query)) {
            statement.setTimestamp(1, Timestamp.valueOf(attendance.getTimeIn()));
            statement.setTimestamp(2, Timestamp.valueOf(attendance.getTimeOut()));
            statement.setString(3, attendance.getRegister().name());
            statement.setLong(4, attendance.getContractor().getContractorId());
            statement.setLong(5, attendance.getAttendanceId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                return Optional.of(attendance);
                
            } else {
                return Optional.empty();
            }
        }
    }
    
    @Override
    public List<Attendance> getAllAttendance() throws SQLException {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance";
        try (Connection con = DBConfig.getCon(); PreparedStatement statement = con.prepareStatement(query); ResultSet rs = statement.executeQuery()) {
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
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE contractor_id = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement statement = con.prepareStatement(query)) {
            statement.setLong(1, contractor.getContractorId());
            ResultSet rs = statement.executeQuery();
            
            while (rs.next()) {
                Attendance attendance = Attendance.builder().build();
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
    
    @Override
    public List<Attendance> retrieveAttendanceByCurrent(ContractPeriod contractPeriod) throws SQLException {
        String query = """
                        SELECT
                            a.attendance_id,
                            a.contractor_id,
                            a.time_in,
                            a.time_out,
                            a.register,
                            c.user_id,
                            c.status,
                            c.contractor_period_id,
                            u.name,
                            u.surname,
                            u.email,
                            u.gender,
                            u.id_number,
                            u.role,
                            u.race,
                            u.location,
                            u.age
                        FROM
                            attendance a
                        JOIN
                            contractor c
                        ON
                            a.contractor_id = c.contractor_id
                        JOIN
                            user u
                        ON
                            c.user_id = u.user_id
                        WHERE
                            c.contractor_period_id = ?
                """;
        List<Attendance> currentAttendance = new ArrayList<>();
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = User.builder()
                            .userId(rs.getLong("user_id"))
                            .name(rs.getString("name"))
                            .surname(rs.getString("surname"))
                            .email(rs.getString("email"))
                            .gender(rs.getString("gender"))
                            .idNumber(rs.getString("id_number"))
                            .role(User.Role.valueOf(rs.getString("role")))
                            .race(rs.getString("race"))
                            .location(rs.getString("location"))
                            .age(rs.getInt("age"))
                            .build();
                    Contractor contractor = Contractor.builder()
                            .user(user)
                            .status(Status.valueOf(rs.getString("status")))
                            .contractPeriod(contractPeriod)
                            .build();
                    Attendance attendance = Attendance.builder()
                            .attendanceId(rs.getLong("attendance_id"))
                            .contractor(contractor)
                            .timeIn(rs.getTimestamp("time_in").toLocalDateTime())
                            .timeOut(rs.getTimestamp("time_out") != null
                                    ? rs.getTimestamp("time_out").toLocalDateTime()
                                    : null)
                            .register(Register.valueOf(rs.getString("register")))
                            .build();
                    currentAttendance.add(attendance);
                }
            }
        }
        return currentAttendance;
    }
    
    public Optional<Attendance> retreiveAttendanceByContractor(Attendance attendance) throws SQLException {
        String query = "SELECT * FROM attendance WHERE contractor_id = ? AND DATE(time_in) = CURDATE()";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, attendance.getContractor().getContractorId());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Long attendanceID = rs.getLong("attendance_id");
                    Contractor contractor = Contractor.builder().contractorId(rs.getLong("contractor_id")).build();
                    LocalDateTime time_in = rs.getTimestamp("time_in").toLocalDateTime();
                    LocalDateTime time_out = null;
                    if (rs.getTimestamp("time_out") != null) {
                        time_out = rs.getTimestamp("time_out").toLocalDateTime();
                    }
                    
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
    
    @Override
    public List<Attendance> todayAttendance() throws SQLException {
        String query = "SELECT * FROM attendance WHERE DATE(time_in) = CURDATE()";
        List<Attendance> todayAttendances = new ArrayList<>();
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contractor contractor = Contractor.builder().contractorId(rs.getLong("contractor_id")).build();
                    Attendance attendance;
                    attendance = Attendance.builder().
                            attendanceId(rs.getLong("attendance_id")).
                            contractor(contractor).
                            timeIn(rs.getTimestamp("time_in").toLocalDateTime()).
                            register(Attendance.Register.valueOf(rs.getString("register"))).
                            build();
                    if (rs.getTimestamp("time_out") != null) {
                        attendance.setTimeOut(rs.getTimestamp("time_out").toLocalDateTime());
                    }
                    todayAttendances.add(attendance);
                }
            }
        }
        return todayAttendances;
    }
}
