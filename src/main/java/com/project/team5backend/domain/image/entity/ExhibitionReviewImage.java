package com.project.team5backend.domain.image.entity;

import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String fileKey;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_review_id")
    private ExhibitionReview exhibitionReview;

    public void deleteImage() {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }
}