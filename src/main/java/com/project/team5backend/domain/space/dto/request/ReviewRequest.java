package com.project.team5backend.domain.space.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

public class ReviewRequest {
    @Getter
    @Setter
    public static class Create {
        private Double rating;
        private String content;
        private List<String> images;
    }
}