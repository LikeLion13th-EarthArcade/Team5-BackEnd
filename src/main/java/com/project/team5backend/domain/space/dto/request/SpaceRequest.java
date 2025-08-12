package com.project.team5backend.domain.space.dto.request;


import com.project.team5backend.domain.space.entity.SpaceMood;
import com.project.team5backend.domain.space.entity.SpacePurpose;
import com.project.team5backend.domain.space.entity.SpaceType;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SpaceRequest {

    // 전시 공간 등록 요청 DTO
    @Getter
    @Setter
    public static class Create {
        private String name;
        private String location;
        private SpaceType type;
        private String spec;
        private SpacePurpose purpose;
        private SpaceMood mood;
        private String businessRegistrationNumber;
        private String description;
        private String businessRegistrationDocUrl;
        private String buildingLedgerDocUrl;
        private List<String> images;
    }

    // 공간 검색 요청 DTO
    @Getter
    @Setter
    public static class Search {
        private String region; // 위치
        private String size; // 크기 및 수용인원
        private SpaceType type; // 전시 유형
        private SpaceMood mood; // 분위기
        private String startDate; // 사용 가능 시작 날짜
        private String endDate;   // 사용 가능 종료 날짜
    }
    @Getter
    @Setter
    public static class Like {
        private Long spaceId;
        private boolean liked;
    }
}
