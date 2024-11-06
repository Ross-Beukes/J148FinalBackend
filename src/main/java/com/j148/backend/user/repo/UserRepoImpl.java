/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.user.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.user.model.User;
import com.j148.backend.user.model.User.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Optional;

/**
 *
 * @author glenl
 */
public class UserRepoImpl extends DBConfig implements UserRepo {

    @Override
    public Optional<User> register(User user) throws SQLException {
        String query = "INSERT INTO user(name, surname, email, gender, id_number, role, race, loaction, age, password) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            con.setAutoCommit(false);
            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getGender());
            ps.setString(5, user.getIdNumber());
            ps.setString(6, user.getRole().name());
            ps.setString(7, user.getRace());
            ps.setString(8, user.getLocation());
            ps.setInt(9, user.getAge());
            ps.setString(10, user.getPassword());

            Savepoint beforeUserInsert = con.setSavepoint();
            if (ps.executeUpdate() > 0) {
                con.commit();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getLong("user_id"));
                    }
                }
                return Optional.of(user);
            } else {
                con.rollback(beforeUserInsert);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> updateUser(User user) throws SQLException {
        String query = "UPDATE user SET name = ?, surname = ?, email = ?, gender = ?, location = ?, password = ? WHERE id_number = ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareCall(query)) {
            con.setAutoCommit(false);
            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getGender());
            ps.setString(5, user.getLocation());
            ps.setString(6, user.getIdNumber());
            ps.setString(7, user.getPassword());

            Savepoint beforeUserEdit = con.setSavepoint();
            if (ps.executeUpdate() > 0) {
                con.commit();
                return Optional.of(user);
            } else {
                con.rollback(beforeUserEdit);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> retreiveUserFromEmail(User user) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getEmail());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long userID = rs.getLong("user_id");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String email = rs.getString("email");
                    String gender = rs.getString("gender");
                    String id_number = rs.getString("id_number");
                    Role role = Role.valueOf(rs.getString("role"));
                    String race = rs.getString("race");
                    String location = rs.getString("location");
                    int age = rs.getInt("age");
                    user.builder().userId(userID).
                            name(name).
                            surname(surname).
                            email(email).
                            gender(gender).
                            idNumber(id_number).
                            role(role).race(race).
                            location(location).
                            age(age).build();
                    return Optional.of(user);

                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> promoteApplicant(User user) throws SQLException {
        String query = "UPDATE user SET role = ? WHERE id_number = ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            con.setAutoCommit(false);
            ps.setString(1, Role.CONTRACTOR.name());
            ps.setString(2, user.getIdNumber());

            Savepoint beforeUserEdit = con.setSavepoint();
            if (ps.executeUpdate() > 0) {
                con.commit();
                return Optional.of(user);
            } else {
                con.rollback(beforeUserEdit);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> retreiveUserFromUserID(User user) throws SQLException {
        String query = "SELECT * FROM user WHERE user_id = ?";
        try (Connection con = getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setLong(1, user.getUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long userID = rs.getLong("user_id");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String email = rs.getString("email");
                    String gender = rs.getString("gender");
                    String id_number = rs.getString("id_number");
                    Role role = Role.valueOf(rs.getString("role"));
                    String race = rs.getString("race");
                    String location = rs.getString("location");
                    int age = rs.getInt("age");
                    user.builder().userId(userID).
                            name(name).
                            surname(surname).
                            email(email).
                            gender(gender).
                            idNumber(id_number).
                            role(role).race(race).
                            location(location).
                            age(age).build();
                    return Optional.of(user);

                }
            }
        }
        return Optional.empty();
    }
}
