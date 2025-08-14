package com.project.team5backend.domain.space.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSpaceRequest {
    private Long id;
    private boolean approve; // true: 승인, false: 거절
    private String reason;   // 거절 시에만 작성
}