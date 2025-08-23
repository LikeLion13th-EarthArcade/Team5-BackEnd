package com.project.team5backend.domain.exhibition.exhibition.entity;

import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Type;
import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.global.converter.FacilityConverter;
import com.project.team5backend.global.entity.Facility;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Exhibition extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String thumbnail;

    private Integer price;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(name = "opening_time")
    private String openingTime;

    @Column(name = "homepage_url")
    private String homepageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Enumerated(EnumType.STRING)
    private Mood mood;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Embedded
    private Address address;

    @Column(name = "rating_avg", columnDefinition = "DECIMAL(4,2) NOT NULL DEFAULT 0")
    private BigDecimal ratingAvg;

    @Column(name = "rating_count")
    private Integer reviewCount;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "total_review_score")
    private Integer totalReviewScore;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    // 시설 정보를 JSON 배열로 저장
    @Convert(converter = FacilityConverter.class)
    @Column(name = "facilities", columnDefinition = "TEXT")
    private List<Facility> facilities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void delete() {
        isDeleted = true;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void resetCount() {
        this.likeCount = 0;
        this.reviewCount = 0;
    }

    public void approveStatus() {
        this.status = Status.APPROVED;
    }

    public void rejectStatus() {
        this.status = Status.REJECTED;
    }
}
