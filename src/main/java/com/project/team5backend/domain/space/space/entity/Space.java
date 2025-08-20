package com.project.team5backend.domain.space.space.entity;


import com.project.team5backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "Space")
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 공간 이름
    private String location; // 공간 위치

    @Enumerated(EnumType.STRING)
    private SpaceType type; // 공간 타입
    private String size; // 공간 크기(면적)

    @Enumerated(EnumType.STRING)
    private SpacePurpose purpose; // 공간 목적

    @Enumerated(EnumType.STRING)
    private SpaceMood mood; // 공간 분위기

    private LocalDate startDate; // 이용시작일
    private LocalDate endDate; // 이용마감일

    private String description; // 공간 설명

    //운영시간
    private String operatingHours;

    @ElementCollection
    private List<String> imageUrls;

    @Enumerated(EnumType.STRING)
    private Status status = Status.APPROVAL_PENDING;

    public enum Status {
        APPROVAL_PENDING,
        APPROVED,
        REJECTED
    }
    @OneToMany(mappedBy = "space", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SpaceLike> spaceLikes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // DB 테이블에 user_id라는 외래키(Foreign Key) 컬럼을 생성합니다.
    private User user;
}