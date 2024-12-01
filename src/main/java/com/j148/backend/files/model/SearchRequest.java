package com.j148.backend.files.model;

import com.j148.backend.user.model.User;
import jakarta.annotation.Nonnull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {

    @Nonnull
    private User user;
    @Nonnull
    private FileEntity fileEntity;

}
