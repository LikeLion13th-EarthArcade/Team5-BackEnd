package com.project.team5backend.domain.space.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

public class AdminSpaceResponse {

    @Getter
    @Setter
    public static class CommonResponse<T> {
        private String status;
        private String message;
        private T data;
        public CommonResponse(String status, String message, T data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
    }

    @Getter
    @Setter
    public static class PendingSpaceListResponse {
        private Long spaceId;
        private String spaceName;
        private String submittedBy;
        private LocalDateTime submittedAt;
    }

    @Getter
    @Setter
    public static class AdminActionResponse {
        private Long id;
        private String status; // APPROVED 또는 REJECTED
    }
}