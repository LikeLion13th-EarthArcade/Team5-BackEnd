package com.project.team5backend.domain.user.me.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class UserMeResponse {

    // -------------------- 내 활동 --------------------
    public record UserActivityResponse(
            List<ExhibitionLike> exhibitionLikes,
            List<SpaceLike> spaceLikes,
            List<Review> reviews
    ) {
        public record ExhibitionLike(Long id, String title, LocalDateTime likedAt) {}
        public record SpaceLike(Long id, String name, LocalDateTime likedAt) {}
        public record Review(Long id, String content, LocalDateTime createdAt) {}
    }

    // -------------------- 내가 등록한 공간 --------------------
    public record UserSpacesResponse(List<Space> spaces) {
        public record Space(Long id, String name, String address) {}
    }

    // -------------------- 내가 등록한 전시 --------------------
    public record UserExhibitionsResponse(List<Exhibition> exhibitions) {
        public record Exhibition(Long id, String title, String status) {}
    }

    // -------------------- 내가 예약한 공간 --------------------
    public record UserReservationsResponse(List<Reservation> reservations) {
        public record Reservation(Long id, String spaceName, String status, LocalDateTime reservedAt) {}
    }
}
