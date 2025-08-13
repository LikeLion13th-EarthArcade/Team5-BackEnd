package com.project.team5backend.domain.exhibition.exhibition.dto.response;

import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Type;
import com.project.team5backend.domain.exhibition.review.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.global.entity.Facility;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class ExhibitionResDTO {

    @Builder
    public record PreviewExhibitionResDTO (
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String openingTime,
            List<String> imageUrls,
            String homepageUrl,
            String address,
            Category category,
            Type type,
            Mood mood,
            Integer price,
            List<Facility> facility
    ) {}

    @Builder
    public record DetailExhibitionResDTO (
            Long exhibitionId,
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String openingTime,
            List<String> imageFileKeys,
            String homepageUrl,
            String address,
            Category category,
            Type type,
            Mood mood,
            Integer price,
            List<Facility> facility,
            List<ExhibitionReviewResDTO.exReviewDetailResDTO> reviews
    ) {}

    @Builder
    public record LikeExhibitionResDTO (
            Long exhibitionId,
            String message
    ){}

    @Builder
    public record SearchExhibitionResDTO (
            Long exhibitionId,
            String title,
            String thumbnail,
            LocalDate startDate,
            LocalDate endDate,
            String address,
            Double latitude,
            Double longitude
    ){}
    @Builder
    public record SearchExhibitionPageResDTO (
            List<SearchExhibitionResDTO> items,
            PageInfo pageInfo,
            MapInfo map // 기본값 - 서울 중심
    ) {
        public record MapInfo(
                Double defaultCenterLat,
                Double defaultCenterLng
        ) {}

        public record PageInfo(
                int page,          // 0-based
                int size,          // 항상 4
                long totalElements,
                int totalPages,
                boolean first,
                boolean last
        ) {}
    }
}
