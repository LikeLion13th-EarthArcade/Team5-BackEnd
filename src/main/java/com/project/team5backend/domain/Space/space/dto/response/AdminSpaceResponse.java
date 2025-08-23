package com.project.team5backend.domain.space.space.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class AdminSpaceResponse {

    public record PendingSpaceListResponse (
            Long spaceId,
            String spaceName,
            String submittedBy
    ){}

    public record AdminActionResponse (
            Long id,
            String status // APPROVED 또는 REJECTED
    ){}
}