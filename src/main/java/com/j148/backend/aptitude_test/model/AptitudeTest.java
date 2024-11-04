package com.j148.backend.aptitude_test.model;
import com.j148.backend.user.model.User;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AptitudeTest {
    private Long aptitudeTestId;
    private int testMark;
    private LocalDateTime testDate;
    private User user;
}
