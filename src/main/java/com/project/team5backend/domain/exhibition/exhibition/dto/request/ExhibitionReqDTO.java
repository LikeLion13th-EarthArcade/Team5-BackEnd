package com.project.team5backend.domain.exhibition.exhibition.dto.request;



import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Type;
import com.project.team5backend.global.address.dto.request.AddressReqDTO;
import com.project.team5backend.global.entity.Facility;
import jakarta.validation.Valid;
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
            @NotNull Category category,
            @NotNull Type type,
            @NotNull Mood mood,
            Integer price,
            List<Facility> facility,
            @NotNull @Valid AddressReqDTO.AddressCreateReqDTO address
    ) {}


}
