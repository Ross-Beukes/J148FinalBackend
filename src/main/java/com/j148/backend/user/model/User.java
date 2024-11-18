package com.j148.backend.user.model;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Min(value = 1, message = "User Id cannot be less than 1")
    private Long userId;
    @NotBlank(message = "A name is required")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "Name must contain only letters")
    private String name;
    @NotBlank(message = "A surname is required")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "Surname must contain only letters")
    private String surname;
    @NotBlank(message = "E-mail is required")
    @Email(message = "Invalid E-mail was provided, Valid E-mail required")
    private String email;
    @NotBlank(message = "A password is required")
    private String password;
    @NotBlank(message = "A gender is required")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "gender must contain only letters")
    private String gender;
    
    @NotBlank(message = "ID number is required")
    @Pattern(regexp = "\\d+", message = "ID number must contain only numbers")
    @Pattern(regexp = "^[0-9]{13}$", message = "National ID must be exactly 13 digits long.")
    private String idNumber;
    private Role role;
    @NotBlank(message = "A race is required")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "Race must contain only letters")
    private String race;
    @NotBlank(message = "A location is required")
    @Pattern(regexp = "[a-zA-Z\\s]+", message = "Location must contain only letters")
    private String location;
    
    @Min(value = 18, message = "Age must at ninimum be 18")
    @Max(value = 100 , message = "Age must be below 100")
    private int age;

    public enum Role {
        APPLICANT, CONTRACTOR, INSTRUCTOR, ADMIN
    }
}