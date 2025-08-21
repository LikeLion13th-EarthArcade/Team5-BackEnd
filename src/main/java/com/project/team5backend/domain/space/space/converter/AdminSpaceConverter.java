package com.project.team5backend.domain.space.space.converter;

import com.project.team5backend.domain.space.space.dto.response.AdminSpaceResponse;
import com.project.team5backend.domain.space.space.entity.Space;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminSpaceConverter {

    // Space 엔티티를 심사 대기 목록 DTO로 변환
    public AdminSpaceResponse.PendingSpaceListResponse toPendingSpaceListResponse(Space space) {
        // NullPointerException을 방지하기 위해 Null 체크를 추가합니다.
        String userEmail = "사용자 정보 없음"; // 사용자 정보가 없을 경우를 위한 기본값

        if (space.getUser() != null) {
            userEmail = space.getUser().getEmail();
        }

        return new AdminSpaceResponse.PendingSpaceListResponse(
                space.getId(), // `space.getSpaceId()`는 존재하지 않으므로 `space.getId()`로 수정
                space.getName(),
                userEmail // 안전하게 변환된 userEmail 사용
                // 다른 필드들...
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
