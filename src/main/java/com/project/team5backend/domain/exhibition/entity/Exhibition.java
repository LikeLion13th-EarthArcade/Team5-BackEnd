package com.project.team5backend.domain.exhibition.entity;

import com.project.team5backend.global.entity.Facility;
import com.project.team5backend.global.entity.embedded.Address;
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

    private Integer price;

    private String startDate;

    private String endDate;

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
    private Status status;

    @Enumerated(EnumType.STRING)
    private Facility facility;
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

}
