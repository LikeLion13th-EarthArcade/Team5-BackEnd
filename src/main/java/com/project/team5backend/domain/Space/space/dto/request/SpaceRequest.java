package com.project.team5backend.domain.space.space.dto.request;


import com.project.team5backend.domain.space.space.entity.SpaceMood;
import com.project.team5backend.domain.space.space.entity.SpacePurpose;
import com.project.team5backend.domain.space.space.entity.SpaceType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class SpaceRequest {

    // 전시 공간 등록 요청 DTO
    public record Create (
            String name,
            String location,
            SpaceType type,
            String spec,
            SpacePurpose purpose,
            SpaceMood mood,
            String startDate,
            String endDate,
            String businessRegistrationNumber,
            String description,
            String businessRegistrationDocUrl,
            String buildingLedgerDocUrl,
            List<String> images
    ){}

    // 공간 검색 요청 DTO
    public record Search (
            String region,
            String size,
            SpaceType type,
            SpaceMood mood,
            String startDate,
            String endDate
    ){}

    public record Like (
            Long spaceId,
            boolean liked
    ){}
}
