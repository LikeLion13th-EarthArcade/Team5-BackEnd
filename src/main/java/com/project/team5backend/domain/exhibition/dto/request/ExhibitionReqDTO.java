package com.project.team5backend.domain.exhibition.dto.request;



import com.project.team5backend.domain.exhibition.entity.Category;
import com.project.team5backend.domain.exhibition.entity.Type;
import com.project.team5backend.global.entity.Facility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class ExhibitionReqDTO {
    public record CreateExhibitionReqDTO (
            @NotBlank String title,
            String description,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @NotBlank String openingHour,
            String homepageUrl,
            @NotBlank String address,
            @NotNull Category category,
            @NotNull Type type,
            Integer price,
            String externalLink,
            List<Facility> facility
    ) {}


}
