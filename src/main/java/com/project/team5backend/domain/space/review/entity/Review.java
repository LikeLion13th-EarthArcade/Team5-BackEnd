package com.project.team5backend.domain.space.review.entity;

import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.global.entity.BaseOnlyCreateTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Review extends BaseOnlyCreateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double rating;

    @Lob
    private String content;

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    private String mainImageKey;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 리뷰 작성자
}