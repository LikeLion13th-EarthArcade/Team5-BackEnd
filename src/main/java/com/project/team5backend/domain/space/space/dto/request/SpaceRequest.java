package com.project.team5backend.domain.space.space.dto.request;


import com.project.team5backend.domain.space.space.entity.SpaceMood;
import com.project.team5backend.domain.space.space.entity.SpacePurpose;
import com.project.team5backend.domain.space.space.entity.SpaceType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class SpaceRequest {

    // 전시 공간 등록 요청 DTO
    public record Create (
            String name,  // 공간 이름
            String location, // 공간 위치
            SpaceType type, // 공간 타입
            String size, // 공간 크기(면적)
            SpacePurpose purpose, // 공간 목적
            SpaceMood mood, // 공간 분위기
            String startDate, // 공간 이용 시작일
            String endDate, // 공간 이용 마감일
            String description, // 공간 설명
            List<String> images
    ){}

    // 공간 검색 요청 DTO
    public record Search (
            String location,
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

    // 전시 공간 정보 수정
    public record UpdateSpace (
            String name,  // 공간 이름
            String location, // 공간 위치
            SpaceType type, // 공간 타입
            String size, // 공간 크기(면적)
            SpacePurpose purpose, // 공간 목적
            SpaceMood mood, // 공간 분위기
            String startDate, // 공간 이용 시작일
            String endDate, // 공간 이용 마감일
            String description, // 공간 설명
            List<String> images
    ){}
}