/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.leave_request.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.files.model.Files;
import com.j148.backend.leave_request.model.LeaveRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yusuf
 */
public class LeaveReqeustRepoImpl extends DBConfig implements LeaveRequestRepo {

    @Override
    public Optional<LeaveRequest> createLeaveRequest(LeaveRequest leaveRequest) throws SQLException {
        String query = "INSERT INTO leave_request (contractor_id, file_id, start_date, end_date, decision) VALUES(?, ?, ?, ?, ?)";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            con.setAutoCommit(false);

            ps.setLong(1, leaveRequest.getContractor().getContractorId());
            ps.setLong(2, leaveRequest.getFile().getFileId());
            ps.setString(3, String.valueOf(leaveRequest.getStartDate()));
            ps.setString(4, String.valueOf(leaveRequest.getEndDate()));
            ps.setString(5, "PENDING");

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
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, contractor.getContractorId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requestMap.put(rs.getLong("leave_request_id"), LeaveRequest.builder().startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate()).decision(LeaveRequest.Decision.valueOf(rs.getString("decision")))
                            .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                            .file(Files.builder().fileId(rs.getLong("file_id")).build())
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
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, String.valueOf(startDate));
            ps.setString(2, String.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    //                requestMap.keySet().add(rs.getLong("leave_request_id"));
                    requestMap.put(rs.getLong("leave_request_id"), LeaveRequest.builder().startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate()).decision(LeaveRequest.Decision.valueOf(rs.getString("decision")))
                            .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                            .file(Files.builder().fileId(rs.getLong("file_id")).build())
                            .build());

                }
            }
        }
        return requestMap;
    }

    @Override
    public Optional<LeaveRequest> updateLeaveRequestToApprovedOrDenied(LeaveRequest leaveRequest) throws SQLException {
        String query = "UPDATE leave_request SET decision = ? WHERE leave_request_id = ?";

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {

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
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
//                requestMap.keySet().add(rs.getLong("leave_request_id"));
                requestMap.put(rs.getLong("leave_request_id"), LeaveRequest.builder().startDate(rs.getDate("start_date").toLocalDate())
                        .endDate(rs.getDate("end_date").toLocalDate()).decision(LeaveRequest.Decision.valueOf(rs.getString("decision")))
                        .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                        .file(Files.builder().fileId(rs.getLong("file_id")).build())
                        .build());

            }
        }
        return requestMap;
    }

    @Override
    public AbstractMap<Long, LeaveRequest> retrieveAllPendingContractorLeaveRequests(Contractor contractor) throws SQLException {
        HashMap<Long, LeaveRequest> requestMap = new HashMap<>();
        String query = "SELECT * FROM leave_request WHERE contractor_id = ? AND decision = \"PENDING\"";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, contractor.getContractorId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requestMap.put(rs.getLong("leave_request_id"), LeaveRequest.builder().startDate(rs.getDate("start_date").toLocalDate())
                            .endDate(rs.getDate("end_date").toLocalDate()).decision(LeaveRequest.Decision.valueOf(rs.getString("decision")))
                            .contractor(Contractor.builder().contractorId(rs.getLong("contractor_id")).build())
                            .file(Files.builder().fileId(rs.getLong("file_id")).build())
                            .build());

                }
            }
        }
        return requestMap;
    }

}
