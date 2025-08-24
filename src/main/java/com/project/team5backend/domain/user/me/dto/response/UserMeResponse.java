package com.project.team5backend.domain.user.me.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UserMeResponse {

        // -------------------- 내 활동 --------------------
        public record UserActivityResponse(
                List<ExhibitionLike> exhibitionLikes,
                List<SpaceLike> spaceLikes,
                List<Review> reviews
        ) {
            // 전시 좋아요
            public record ExhibitionLike(
                    Long id,
                    String ExhibitionName,
                    String type,          // 전시 타입
                    String mood,          // 전시 무드
                    String thumbnailUrl  // 전시 썸네일
            ) {}

            // 전시공간 좋아요
            public record SpaceLike(
                    Long id,
                    String spaceName,
                    String address,
                    String thumbnailUrl  // 공간 썸네일
            ) {}

            // 리뷰
            public record Review(
                    Long id,
                    String Name,     // 작성자 이름
                    String content,        // 리뷰 내용
                    LocalDateTime createdAt
            ) {}
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
        public record UserReservationsResponse(
                List<Reservation> reservations
        ) {
            public record Reservation(
                    Long reservationId,
                    String spaceName,
                    String status,
                    LocalDateTime reservedAt
            ) {}
        }
    }
