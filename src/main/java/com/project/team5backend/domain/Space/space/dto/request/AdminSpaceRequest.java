package com.project.team5backend.domain.space.space.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSpaceRequest {
    public record AdminSpaceReq(
            Long id,
            boolean approve, // true: 승인, false: 거절
            String reason   // 거절 시에만 작성
    ){}  // 거절 시에만 작성
}