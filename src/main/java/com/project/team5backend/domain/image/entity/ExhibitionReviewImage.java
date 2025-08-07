package com.project.team5backend.domain.image.entity;

import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionReviewImage extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_review_image_id")
    private Long id;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_review_id")
    private ExhibitionReview exhibitionReview;

}