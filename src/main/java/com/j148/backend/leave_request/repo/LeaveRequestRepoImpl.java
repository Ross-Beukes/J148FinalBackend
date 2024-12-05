/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.leave_request.model.LeaveRequest;
import com.j148.backend.user.model.User;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author yusuf
 */
public class LeaveRequestRepoImpl implements LeaveRequestRepo {
    
    @Inject
    private DBConfig DBConfig;

    @Override
    public Optional<LeaveRequest> createLeaveRequest(LeaveRequest leaveRequest) throws SQLException {
        String query = "INSERT INTO leave_request (contractor_id, start_date, end_date, decision) VALUES(?, ?, ?, ?)";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, leaveRequest.getContractor().getContractorId());
            ps.setString(2, String.valueOf(leaveRequest.getStartDate()));
            ps.setString(3, String.valueOf(leaveRequest.getEndDate()));
            ps.setString(4, "PENDING");

            Savepoint beforeReservationInput = con.setSavepoint();
            if (ps.executeUpdate() > 0) {
                con.commit();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        leaveRequest.setLeaveRequestId(keys.getLong(1));
                    }
                }

                return Optional.of(leaveRequest);
            } else {
                con.rollback(beforeReservationInput);
            }
        }
        return Optional.empty();
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAllContractorLeaveRequests(Contractor contractor) throws SQLException {
        HashMap<Long, LeaveRequest> requestMap = new HashMap<>();
        String query = "SELECT * FROM leave_request WHERE contractor_id = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, contractor.getContractorId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requestMap.put(rs.getLong("leave_request_id"), LeaveRequest.builder().startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate()).decision(LeaveRequest.Decision.valueOf(rs.getString("decision")))
                            .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                            .file(FileEntity.builder().fileId(rs.getLong("file_id")).build())
                            .build());

                }
            }
        }
        return requestMap;
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveLeaveRequestsByStartAndEndDate(LocalDate startDate, LocalDate endDate) throws SQLException {
        HashMap<Long, LeaveRequest> requestMap = new HashMap<>();
        String query = "SELECT * FROM leave_request WHERE start_date > ? AND end_date < ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, String.valueOf(startDate));
            ps.setString(2, String.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    //                requestMap.keySet().add(rs.getLong("leave_request_id"));
                    requestMap.put(rs.getLong("leave_request_id"), LeaveRequest.builder().startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate()).decision(LeaveRequest.Decision.valueOf(rs.getString("decision")))
                            .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                            .file(FileEntity.builder().fileId(rs.getLong("file_id")).build())
                            .build());

                }
            }
        }
        return requestMap;
    }

    @Override
    public Optional<LeaveRequest> updateLeaveRequestToApprovedOrDenied(LeaveRequest leaveRequest) throws SQLException {
        String query = "UPDATE leave_request SET decision = ? WHERE leave_request_id = ?";

        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {

            Savepoint beforeReservationInput = con.setSavepoint();

            ps.setString(1, leaveRequest.getDecision().toString());
            ps.setLong(2, leaveRequest.getLeaveRequestId());

            con.setAutoCommit(false);

            if (ps.executeUpdate() > 0) {
                con.commit();

                return Optional.of(leaveRequest);
            } else {
                con.rollback(beforeReservationInput);
            }
        }
        return Optional.empty();
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAll() throws SQLException {
        HashMap<Long, LeaveRequest> requestMap = new HashMap<>();
        String query = "SELECT * FROM leave_request";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requestMap.put(rs.getLong("leave_request_id"), LeaveRequest.builder().startDate(rs.getDate("start_date").toLocalDate())
                        .endDate(rs.getDate("end_date").toLocalDate()).decision(LeaveRequest.Decision.valueOf(rs.getString("decision")))
                        .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                        .file(FileEntity.builder().fileId(rs.getLong("file_id")).build())
                        .build());

            }
        }
        return requestMap;
    }
	
    @Override
    public List<LeaveRequest> retrieveAllLeaveRequest() throws SQLException {
        List<LeaveRequest> leaveRequests = new ArrayList<>();
        String query = "SELECT " +
                "lr.leave_request_id"+
                "lr.start_date, " +
                "lr.end_date, " +
                "lr.decision," +
                "f.file_id, " +
                "f.category " +
                "FROM leave_request lr " +
                "JOIN contractor c ON lr.contractor_id = c.contractor_id " +
                "JOIN user u ON c.user_id = u.user_id " +
                "JOIN files f ON lr.file_id = f.file_id";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LeaveRequest leaveRequest = LeaveRequest.builder()
                        .leaveRequestId(rs.getLong("leave_request_id"))
                        .startDate(rs.getDate("start_date").toLocalDate())
                        .endDate(rs.getDate("end_date").toLocalDate())
                        .decision(LeaveRequest.Decision.valueOf(rs.getString("decision").toUpperCase()))
                        .contractor(Contractor.builder()
                                .user(User.builder()
                                        .name(rs.getString("name"))
                                        .email(rs.getString("email"))
                                        .build())
                                .build())
                        .file(FileEntity.builder()
                                .build())
                        .build();

                leaveRequests.add(leaveRequest);
            }
        }
        return leaveRequests;
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAllPendingContractorLeaveRequests(Contractor contractor) throws SQLException {
        HashMap<Long, LeaveRequest> requestMap = new HashMap<>();
        String query = "SELECT * FROM leave_request WHERE contractor_id = ? AND decision = \"PENDING\"";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, contractor.getContractorId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requestMap.put(rs.getLong("leave_request_id"), LeaveRequest.builder().startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate()).decision(LeaveRequest.Decision.valueOf(rs.getString("decision")))
                            .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                            .file(FileEntity.builder().fileId(rs.getLong("file_id")).build())
                            .build());

                }

            }
        }
        return requestMap;
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAllLeaveRequestsByDecision(String decision) throws SQLException {
        HashMap<Long, LeaveRequest> requestMap = new HashMap<>();
        String query = "SELECT * FROM leave_request WHERE decision = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, LeaveRequest.Decision.valueOf(decision).toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requestMap.put(rs.getLong("leave_request_id"), LeaveRequest.builder().startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate()).decision(LeaveRequest.Decision.valueOf(rs.getString("decision")))
                            .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                            .file(FileEntity.builder().fileId(rs.getLong("file_id")).build())
                            .build());

                }

            }
        }
        return requestMap;
    }

    @Override
    public Optional<LeaveRequest> retrieveLeaveRequestByID(LeaveRequest leaveRequest) throws SQLException {
        String query = "SELECT * FROM leave_request WHERE leave_request_id = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, leaveRequest.getLeaveRequestId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LeaveRequest foundRequest = LeaveRequest.builder().leaveRequestId(rs.getLong("leave_request_id")).startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate()).decision(LeaveRequest.Decision.valueOf(rs.getString("decision")))
                            .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                            .file(FileEntity.builder().fileId(rs.getLong("file_id")).build())
                            .build();
                    return Optional.of(foundRequest);
                }

            }
        }
        return Optional.empty();
    }

}
