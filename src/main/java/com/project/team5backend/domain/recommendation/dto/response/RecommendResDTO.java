package com.project.team5backend.domain.recommendation.dto.response;

import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;

import java.util.List;

public class RecommendResDTO {
    public record PersonalizedSummaryResDTO(
            boolean eligible,
            Long previewExhibitionId,
            String previewTitle,
            String previewThumbnailUrl
    ) {}
    public record PersonalizedDetailResDTO(
            Category topCategory,
            Mood topMood,
            List<ExhibitionResDTO.ExhibitionCardResDTO> items
    ) {}
}
