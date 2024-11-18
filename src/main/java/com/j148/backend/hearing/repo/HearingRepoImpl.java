/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.hearing.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.contractor.model.Contractor;
import com.j148.backend.hearing.model.Hearing;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author arshr
 */
public class HearingRepoImpl extends DBConfig implements HearingRepo {

    @Override
    public Optional<Hearing> createHearing(Hearing hearing) throws SQLException {
        String query = "INSERT INTO hearings(contractor_id, schedule_date, outcome, reason) VALUES(?,?,?,?)";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, hearing.getContractor().getContractorId());
            ps.setTimestamp(2, Timestamp.valueOf(hearing.getScheduleDate()));
            ps.setString(3, hearing.getOutcome().name());
            ps.setString(4, hearing.getReason());
            Savepoint beforeHearingInsert = con.setSavepoint();

            if (ps.executeUpdate() > 0) {
                con.commit();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        hearing.setHearingsId(rs.getLong(1));

                    }
                }
                return Optional.of(hearing);
            } else {
                con.rollback(beforeHearingInsert);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Hearing> updateHearing(Hearing hearing) throws SQLException {
        String query;
        System.out.println(hearing);
        query = "UPDATE hearings SET contractor_id = ?, schedule_date = ?, outcome = ?, reason = ? WHERE hearings_id = ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            ps.setLong(1, hearing.getContractor().getContractorId());
            ps.setTimestamp(2, Timestamp.valueOf(hearing.getScheduleDate()));
            ps.setString(3, hearing.getOutcome().name());
            ps.setString(4, hearing.getReason());
            ps.setLong(5, hearing.getHearingsId());

            Savepoint beforeChangingContractorId = con.setSavepoint();
            if (ps.executeUpdate() > 0) {
                con.commit();
                return Optional.of(hearing);
            } else {
                con.rollback(beforeChangingContractorId);
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Hearing> getHearing(Hearing hearing) throws SQLException {
        String query = "SELECT * FROM hearings WHERE hearings_id = ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, hearing.getHearingsId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long hearingId = rs.getLong("hearings_id");
                    Contractor contractor = Contractor.builder()
                            .contractorId(rs.getLong("contractor_id"))
                            .build();
                    LocalDateTime schedule_date = rs.getTimestamp("schedule_date").toLocalDateTime();
                    Hearing.Outcome outcome = Hearing.Outcome.valueOf(rs.getString("outcome"));
                    String reason = rs.getString("reason");

                    Hearing retrievedHearing = Hearing.builder()
                            .hearingsId(hearingId)
                            .contractor(contractor)
                            .scheduleDate(schedule_date)
                            .outcome(outcome)
                            .reason(reason)
                            .build();

                    return Optional.of(retrievedHearing);
                }
            }

        }
        return Optional.empty();

    }

    @Override
    public List<Hearing> findAllHearings() throws SQLException {

        String query = "SELECT * FROM hearings";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Hearing> listOfHearings = new ArrayList<>();
                while (rs.next()) {
                    long hearingId = rs.getLong("hearing_id");
                    Contractor contractor = Contractor.builder()
                            .contractorId(rs.getLong("contactor_id"))
                            .build();
                    LocalDateTime schedule_date = rs.getTimestamp("schedule_date").toLocalDateTime();
                    Hearing.Outcome outcome = Hearing.Outcome.valueOf(rs.getString("outcome"));
                    String reason = rs.getString("reason");

                    Hearing retrievedHearing = Hearing.builder()
                            .hearingsId(hearingId)
                            .contractor(contractor)
                            .scheduleDate(schedule_date)
                            .outcome(outcome)
                            .reason(reason)
                            .build();

                    listOfHearings.add(retrievedHearing);
                }

                return listOfHearings;

            }

        }
    }

    @Override
    public List<Hearing> findAllHearingsForIndividual(Hearing hearing) throws SQLException {
        String query = "SELECT * FROM hearings WHERE contractor_id = ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, hearing.getContractor().getContractorId());
            try (ResultSet rs = ps.executeQuery()) {
                List<Hearing> listOfHearingsForPerson = new ArrayList<>();
                while (rs.next()) {
                    long hearingId = rs.getLong("hearing_id");
                    Contractor contractor = Contractor.builder()
                            .contractorId(rs.getLong("contactor_id"))
                            .build();
                    LocalDateTime schedule_date = rs.getTimestamp("schedule_date").toLocalDateTime();
                    String outcomeString = rs.getString("outcome");
                    Hearing.Outcome outcome = Hearing.Outcome.valueOf(rs.getString("outcome"));
                    String reason = rs.getString("reason");

                    Hearing retrievedHearing = Hearing.builder()
                            .hearingsId(hearingId)
                            .contractor(contractor)
                            .scheduleDate(schedule_date)
                            .outcome(outcome)
                            .reason(reason)
                            .build();

                    listOfHearingsForPerson.add(retrievedHearing);
                }

                return listOfHearingsForPerson;

            }

        }
    }

    @Override
    public List<Hearing> findUpcomingHearings() throws SQLException {

        String query = "SELECT * FROM hearings WHERE schedule_date > ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            try (ResultSet rs = ps.executeQuery()) {
                List<Hearing> listOfHearings = new ArrayList<>();
                while (rs.next()) {
                    long hearingId = rs.getLong("hearing_id");
                    Contractor contractor = Contractor.builder()
                            .contractorId(rs.getLong("contactor_id"))
                            .build();
                    LocalDateTime schedule_date = rs.getTimestamp("schedule_date").toLocalDateTime();
                    String outcomeString = rs.getString("outcome");
                    Hearing.Outcome outcome = Hearing.Outcome.valueOf(rs.getString("outcome"));
                    String reason = rs.getString("reason");

                    Hearing retrievedHearing = Hearing.builder()
                            .hearingsId(hearingId)
                            .contractor(contractor)
                            .scheduleDate(schedule_date)
                            .outcome(outcome)
                            .reason(reason)
                            .build();

                    listOfHearings.add(retrievedHearing);
                }

                return listOfHearings;

            }

        }
    }

    @Override
    public List<Hearing> findHearingsWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {

        String query = "SELECT * FROM hearings WHERE schedule_date BETWEEN ? AND ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                List<Hearing> listOfHearings = new ArrayList<>();
                while (rs.next()) {
                    long hearingId = rs.getLong("hearing_id");
                    Contractor contractor = Contractor.builder()
                            .contractorId(rs.getLong("contactor_id"))
                            .build();
                    LocalDateTime schedule_date = rs.getTimestamp("schedule_date").toLocalDateTime();
                    String outcomeString = rs.getString("outcome");
                    Hearing.Outcome outcome = Hearing.Outcome.valueOf(rs.getString("outcome"));
                    String reason = rs.getString("reason");

                    Hearing retrievedHearing = Hearing.builder()
                            .hearingsId(hearingId)
                            .contractor(contractor)
                            .scheduleDate(schedule_date)
                            .outcome(outcome)
                            .reason(reason)
                            .build();

                    listOfHearings.add(retrievedHearing);
                }

                return listOfHearings;

            }

        }
    }

    @Override
    public List<Hearing> findContractorHearingHistory(Contractor contractor) throws SQLException {
        String sql = "SELECT * from hearings WHERE contractor_id = ?";
        List<Hearing> hearingHistory = new ArrayList<>();

        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, contractor.getContractorId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    long hearingsid = rs.getLong("hearings_id");
                    LocalDateTime scheduleDate = rs.getTimestamp("schedule_date").toLocalDateTime();
                    Hearing.Outcome outcome = Hearing.Outcome.valueOf(rs.getString("outcome"));
                    String reason = rs.getString("reason");

                    Hearing retrievedHearing = Hearing.builder()
                            .hearingsId(hearingsid)
                            .contractor(contractor)
                            .scheduleDate(scheduleDate)
                            .outcome(outcome)
                            .reason(reason)
                            .build();
                    // null checks in front end not there don't show
                    hearingHistory.add(retrievedHearing);
                }
            }
        }

        return hearingHistory;

    }

}
