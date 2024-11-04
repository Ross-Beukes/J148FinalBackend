package com.j148.backend.files.model;
import com.j148.backend.user.model.User;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Files {
    private Long fileId;
    private String fileType;
    private Category category;
    private LocalDateTime dateAdded;
    private String path;
    private User user;
    private Verified verified;

    public enum Category {
        TIMESHEET, MATRIC, ID, CONTRACT, LEAVE_FORM, PROJECT, OTHER
    }

    public enum Verified {
        APPROVED, REJECTED, WAITING, NOT_APPLICABLE
    }
}