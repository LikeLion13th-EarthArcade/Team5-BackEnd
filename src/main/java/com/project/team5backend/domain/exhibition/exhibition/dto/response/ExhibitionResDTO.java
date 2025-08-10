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
            List<ExhibitionReviewResDTO.exReviewPreviewResDTO> reviews
    ) {}

    @Builder
    public record LikeExhibitionResDTO (
            Long exhibitionId,
            String message
    ){}
}
