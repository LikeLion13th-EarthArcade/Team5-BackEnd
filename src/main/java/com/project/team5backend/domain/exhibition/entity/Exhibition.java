package com.project.team5backend.domain.exhibition.entity;

import com.project.team5backend.global.embedded.Address;
import com.project.team5backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Exhibition extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String thumbnail;

    private String startDate;

    private String endDate;

    @Column(name = "opening_time")
    private String openingTime;

    @Column(name = "homepage_url")
    private String homepageUrl;

    @Embedded
    private Address address;

    @Column(name = "rating_avg")
    private double ratingAvg;

    @Column(name = "rating_count")
    private Integer reviewCount;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    private Status status;
}
