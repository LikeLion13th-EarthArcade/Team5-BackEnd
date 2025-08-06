package com.project.team5backend.domain.exhibition.exhibition.dto.response;

import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Type;
import com.project.team5backend.global.entity.Facility;
import com.project.team5backend.global.entity.embedded.Address;

import java.time.LocalDate;
import java.util.List;

public class ExhibitionResDTO {
    public record PreviewExhibitionResDTO (
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String openingHour,
            Address address,
            Category category,
            Type type,
            Integer price,
            String externalLink,
            List<Facility> facility
    ) {}

}
