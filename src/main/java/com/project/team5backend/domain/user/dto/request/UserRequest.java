package com.project.team5backend.domain.user.dto.request;


import lombok.*;

public class UserRequest {

    @Getter @Setter
    @NoArgsConstructor
    public static class SignUp {
        private String email;
        private String password;
        private String name;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class Login {
        private String email;
        private String password;
    }
}

