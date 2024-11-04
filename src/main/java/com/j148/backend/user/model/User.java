package com.j148.backend.user.model;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long userId;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String gender;
    private String idNumber;
    private Role role;
    private String race;
    private String location;
    private int age;

    public enum Role {
        APPLICANT, CONTRACTOR, INSTRUCTOR, ADMIN
    }
}