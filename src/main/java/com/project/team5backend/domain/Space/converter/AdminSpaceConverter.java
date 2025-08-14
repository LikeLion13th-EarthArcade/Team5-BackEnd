package com.project.team5backend.domain.space.converter;

import com.project.team5backend.domain.space.dto.response.AdminSpaceResponse;
import com.project.team5backend.domain.space.entity.Space;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminSpaceConverter {

    // Space 엔티티를 심사 대기 목록 DTO로 변환
    public AdminSpaceResponse.PendingSpaceListResponse toPendingSpaceListResponse(Space space) {
        return new AdminSpaceResponse.PendingSpaceListResponse(
                space.getId(),
                space.getName(),
                space.getSubmittedBy(),
                space.getSubmittedAt()
        );
    }

    // Space 엔티티 리스트를 심사 대기 목록 DTO 리스트로 변환
    public List<AdminSpaceResponse.PendingSpaceListResponse> toPendingSpaceListResponseList(List<Space> spaces) {
        return spaces.stream()
                .map(this::toPendingSpaceListResponse)
                .collect(Collectors.toList());
    }

    // 업데이트된 Space 엔티티를 관리자 응답 DTO로 변환
    public AdminSpaceResponse.AdminActionResponse toAdminActionResponse(Space space) {
        return new AdminSpaceResponse.AdminActionResponse(
                space.getId(),
                space.getStatus().name()
        );
    }
}
