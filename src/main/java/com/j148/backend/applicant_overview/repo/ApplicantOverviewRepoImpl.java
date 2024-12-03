/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.applicant_overview.repo;

import com.j148.backend.applicant_overview.model.ApplicantDocuments;
import com.j148.backend.applicant_overview.model.ApplicantOverview;
import com.j148.backend.aptitude_test.model.AptitudeTest;
import com.j148.backend.config.DBConfig;
import com.j148.backend.files.model.FileEntity;
import com.j148.backend.user.model.User;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Yusuf
 */
public class ApplicantOverviewRepoImpl implements ApplicantOverviewRepo {

    @Inject
    private DBConfig DBConfig;

    @Override
    public List<ApplicantOverview> getAllApplicantOverview() throws SQLException {

        List<ApplicantOverview> allApplicants = new ArrayList<>();
        Map<Long, ApplicantOverview> applicantMap = new HashMap<>();

        String query = "SELECT "
                + "user.user_id, user.name AS user_name, user.surname, user.email, user.age, user.gender, user.race, user.id_number, user.location, "
                + "aptitude_test.aptitude_test_id, aptitude_test.test_mark, aptitude_test.test_date, "
                + "id_file.file_id AS id_file_id, id_file.file_type AS id_file_type, id_file.file_size AS id_file_size, id_file.verified AS id_verification_status, id_file.date_added AS id_date_added, "
                + "matric_file.file_id AS matric_file_id, matric_file.file_type AS matric_file_type, matric_file.file_size AS matric_file_size, matric_file.verified AS matric_verification_status, matric_file.date_added AS matric_date_added, "
                + "contract_file.file_id AS contract_file_id, contract_file.file_type AS contract_file_type, contract_file.file_size AS contract_file_size, contract_file.verified AS contract_verification_status, contract_file.date_added AS contract_date_added "
                + "FROM user "
                + "LEFT JOIN aptitude_test ON user.user_id = aptitude_test.user_id "
                + "LEFT JOIN files AS id_file ON user.user_id = id_file.user_id AND id_file.category = 'ID' "
                + "LEFT JOIN files AS matric_file ON user.user_id = matric_file.user_id AND matric_file.category = 'MATRIC_CERTIFICATE' "
                + "LEFT JOIN files AS contract_file ON user.user_id = contract_file.user_id AND contract_file.category = 'CONTRACT' "
                + "WHERE user.role = 'APPLICANT' "
                + "ORDER BY "
                + "GREATEST(IFNULL(id_file.date_added, '1970-01-01'), IFNULL(matric_file.date_added, '1970-01-01'), IFNULL(contract_file.date_added, '1970-01-01')) DESC, "
                + "CASE "
                + "    WHEN id_file.verified = 'APPROVED' AND matric_file.verified = 'APPROVED' AND contract_file.verified = 'APPROVED' "
                + "    THEN GREATEST(IFNULL(id_file.date_added, '1970-01-01'), IFNULL(matric_file.date_added, '1970-01-01'), IFNULL(contract_file.date_added, '1970-01-01')) "
                + "    ELSE NULL "
                + "END DESC, "
                + "CASE "
                + "    WHEN id_file.verified != 'APPROVED' OR matric_file.verified != 'APPROVED' OR contract_file.verified != 'APPROVED' "
                + "    THEN GREATEST(IFNULL(id_file.date_added, '1970-01-01'), IFNULL(matric_file.date_added, '1970-01-01'), IFNULL(contract_file.date_added, '1970-01-01')) "
                + "    ELSE NULL "
                + "END DESC;";

        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long applicantId = rs.getLong("user_id");

                    ApplicantOverview applicantOverview = applicantMap.get(applicantId);

                    if (applicantOverview == null) {
                        applicantOverview = new ApplicantOverview();
                        ApplicantDocuments applicantDocuments = new ApplicantDocuments();

                        User user = User.builder()
                                .userId(rs.getLong("user_id"))
                                .name(rs.getString("user_name"))
                                .idNumber(rs.getString("id_number"))
                                .location(rs.getString("location"))
                                .surname(rs.getString("surname"))
                                .email(rs.getString("email"))
                                .age(rs.getInt("age"))
                                .race(rs.getString("race"))
                                .gender(rs.getString("gender"))
                                .build();
                        applicantDocuments.setUser(user);

                        if (rs.getLong("id_file_id") != 0) {
                            FileEntity idFile = FileEntity.builder()
                                    .fileId(rs.getLong("id_file_id"))
                                    .fileType(rs.getString("id_file_type"))
                                    .fileSize(rs.getInt("id_file_size"))
                                    .verified(FileEntity.Verified.valueOf(rs.getString("id_verification_status")))
                                    .dateAdded((rs.getTimestamp("id_date_added")).toLocalDateTime()).build();

                            applicantDocuments.setIdFile(idFile);
                        } else {
                            applicantDocuments.setIdFile(null);
                        }

                        if (rs.getLong("matric_file_id") != 0) {
                            FileEntity matricFile = FileEntity.builder()
                                    .fileId(rs.getLong("matric_file_id"))
                                    .fileType(rs.getString("matric_file_type"))
                                    .fileSize(rs.getInt("matric_file_size"))
                                    .verified(FileEntity.Verified.valueOf(rs.getString("matric_verification_status")))
                                    .dateAdded((rs.getTimestamp("matric_date_added")).toLocalDateTime()).build();

                            applicantDocuments.setMatricFile(matricFile);
                        } else {
                            applicantDocuments.setMatricFile(null);
                        }

                        if (rs.getLong("contract_file_id") != 0) {
                            FileEntity contractFile = FileEntity.builder()
                                    .fileId(rs.getLong("contract_file_id"))
                                    .fileType(rs.getString("contract_file_type"))
                                    .fileSize(rs.getInt("contract_file_size"))
                                    .verified(FileEntity.Verified.valueOf(rs.getString("contract_verification_status")))
                                    .dateAdded((rs.getTimestamp("contract_date_added")).toLocalDateTime()).build();

                            applicantDocuments.setContractFile(contractFile);
                        } else {
                            applicantDocuments.setContractFile(null);
                        }
                        
                        AptitudeTest aptitudeTest;
                        if (rs.getLong("aptitude_test_id") != 0L) {
                        aptitudeTest = AptitudeTest.builder()
                                .aptitudeTestId(rs.getLong("aptitude_test_id"))
                                .testMark(rs.getInt("test_mark"))
                                .testDate((rs.getTimestamp("test_date")).toLocalDateTime()).build();
                        } else {
                            aptitudeTest = null;
                        }

                        applicantOverview.setDocuments(applicantDocuments);
                        applicantOverview.setAptitudeTest(aptitudeTest);

                        applicantMap.put(applicantId, applicantOverview);
                    }
                }
            }
        }
        allApplicants.addAll(applicantMap.values());
        return allApplicants;
    }

}
