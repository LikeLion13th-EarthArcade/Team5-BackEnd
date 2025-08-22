package com.project.team5backend.domain.image.entity;

import com.project.team5backend.domain.space.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileKey; // S3에 저장된 파일 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
    @Column(name = "is_deleted")
    private boolean isDeleted;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

}

