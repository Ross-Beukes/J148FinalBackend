package com.j148.backend.files.model;
import com.j148.backend.user.model.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity {
    @Min(value = 1, message = "Leave request ID cannot be less than 1")
    private Long fileId;
    @NotBlank(message = "A file type is required")
    private String fileType;
    private Category category;
    private LocalDateTime dateAdded;
    @NotBlank(message = "A file path is required")
    private String path;
    private User user;
    private Verified verified;

    public enum Category {
        TIMESHEET, MATRIC_CERTIFICATE, ID, CONTRACT, LEAVE_FORM, PROJECT, OTHER
    }

    public enum Verified {
        APPROVED, REJECTED, WAITING, NOT_APPLICABLE
    }
}