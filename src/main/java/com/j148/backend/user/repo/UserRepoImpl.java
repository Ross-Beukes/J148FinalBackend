/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.j148.backend.user.repo;

import com.j148.backend.config.DBConfig;
import com.j148.backend.user.model.User;
import com.j148.backend.user.model.User.Role;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author glenl
 */
@ApplicationScoped

public class UserRepoImpl  implements UserRepo {
@Inject
private DBConfig DBConfig;
    

    @Override
    public Optional<User> register(User user) throws SQLException {
        String query = "INSERT INTO user(name, surname, email, gender, id_number, role, race, location, age, password) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

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


            if (ps.executeUpdate() > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        //the testing throws a sql error when using the column name for this field.
                        user.setUserId(rs.getLong(1)); //1 is the user_id in the user table.
                    }
                }
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> updateUser(User user) throws SQLException {
        String query = "UPDATE user SET name = ?, surname = ?, email = ?, gender = ?, location = ?, password = ? WHERE id_number = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareCall(query)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getGender());
            ps.setString(5, user.getLocation());
            ps.setString(6, user.getPassword());
            ps.setString(7, user.getIdNumber());

            if (ps.executeUpdate() > 0) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> retreiveUserFromEmail(User user) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ?";
        User foundUser;
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
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
                    String password = rs.getString("password");
                    int age = rs.getInt("age");
                    foundUser = User.builder().userId(userID).
                            name(name).
                            surname(surname).
                            email(email).
                            gender(gender).
                            idNumber(id_number).
                            role(role).race(race).
                            location(location).
                            age(age).password(password).build();
                    return Optional.of(foundUser);

                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> promoteApplicant(User user) throws SQLException {
        String query = "UPDATE user SET role = ? WHERE id_number = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, Role.CONTRACTOR.name());
            ps.setString(2, user.getIdNumber());
            if (ps.executeUpdate() > 0) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> retrieveUserFromUserID(User user) throws SQLException {
        String query = "SELECT * FROM user WHERE user_id = ?";
        User foundUser;
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
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
                    foundUser = User.builder().userId(userID).
                            name(name).
                            surname(surname).
                            email(email).
                            gender(gender).
                            idNumber(id_number).
                            role(role).race(race).
                            location(location).
                            age(age).build();
                    return Optional.of(foundUser);

                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> retrieveAllUsers() throws SQLException {
        List<User> allUsers = new ArrayList<>();
        String query = "SELECT * FROM user";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();

                user.setUserId(rs.getLong("user_id"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setGender(rs.getString("gender"));
                user.setIdNumber(rs.getString("id_number"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                user.setRace(rs.getString("race"));
                user.setLocation(rs.getString("location"));
                user.setAge(rs.getInt("age"));

                allUsers.add(user);
            }
        }
        return allUsers;
    }

    @Override
    public Optional<User> updateAge(User user) throws SQLException {
        String query = "UPDATE user SET age = ? WHERE id_number = ?";
        try (Connection con =DBConfig.getCon(); PreparedStatement ps = con.prepareCall(query)) {
            ps.setInt(1, user.getAge());
            ps.setString(2, user.getIdNumber());

            if (ps.executeUpdate() > 0) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getAdmin() throws SQLException {
        String query = "SELECT * FROM user WHERE role = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, Role.ADMIN.name());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setGender(rs.getString("gender"));
                user.setIdNumber(rs.getString("id_number"));
                user.setRole(User.Role.valueOf(rs.getString("role")));
                user.setRace(rs.getString("race"));
                user.setLocation(rs.getString("location"));
                user.setAge(rs.getInt("age"));
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> promoteStaff(User user) throws SQLException {
        String query = "UPDATE user SET role = ? WHERE email = ?";
        try (Connection con = DBConfig.getCon(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getRole().toString());
            ps.setString(2, user.getEmail());
            if (ps.executeUpdate() > 0) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
