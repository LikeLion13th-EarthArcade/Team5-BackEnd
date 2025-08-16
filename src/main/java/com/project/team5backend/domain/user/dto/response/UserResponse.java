package com.project.team5backend.domain.user.dto.response;

import lombok.*;

import java.time.LocalDateTime;


@Getter @Setter
@AllArgsConstructor
public class UserResponse {

    public record LoginResult (
            Long userId,
            String name,
            String email
        ){}
    public record MyInfo (
            Long userId,
            String name,
            String email,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ){}
}



