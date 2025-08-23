package com.project.team5backend.domain.space.review.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ReviewRequest {

    public record CreateRe(
            Double rating,
            String content,
            List<String> images
    ){}
}