package com.j148.backend.aptitude_test.model;
import com.j148.backend.user.model.User;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AptitudeTest {
    @NotNull(message = "Aptitude test ID cannot be Null !")
    @Min(value = 1, message = "Test ID cannot be less than 1")
    private Long aptitudeTestId;
    @Min(value = 0, message = "Test mark cannot be less than 0")
    @Max(value = 100 , message = "Test mark cannot exceed 100")
    private int testMark;
    private LocalDateTime testDate;
    private User user;
}
