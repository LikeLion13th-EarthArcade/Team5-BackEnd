package com.project.team5backend.domain.user.dto.request;


import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class UserRequest {

    public record Login(
            String email,
            String password
    ) {}
    public record SignUp(
            String email,
            String password,
            String name
    ) {}
    public record UpdateUserInfo(
            String newName,
            String currentPassword,
            String newPassword,
            String newPasswordConfirmation
    ){}
}

