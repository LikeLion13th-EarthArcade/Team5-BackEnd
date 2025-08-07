package com.project.team5backend.domain.user.dto.response;

import lombok.*;

public class UserResponse {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginResult {
        private Long userId;
        private String name;
        private String email;
    }

    @Getter
    @AllArgsConstructor
    public static class MyInfo {
        private String email;
        private String name;
    }
    @Getter
    @AllArgsConstructor
    public static class CommonResponse {
        private String status;
        private String message;
        private Object data;
    }
}

