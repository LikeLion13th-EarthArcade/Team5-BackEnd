package com.project.team5backend.domain.space.space.entity;


import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.global.entity.embedded.Address;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
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
    // ✅ 여기에 사용자(User)와 공간(Space)의 관계를 정의합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // DB 테이블에 user_id라는 외래키(Foreign Key) 컬럼을 생성합니다.
    private User user;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 공간 이름

    @Embedded
    private Address address; // 공간 주소 ✅✅✅

    @Enumerated(EnumType.STRING)
    private SpaceType type;     // 공간 유형 (전시회, 팝업스토어, 체험전시)

    @Enumerated(EnumType.STRING)
    private SpaceSize size;     // 공간 크기 (SMALL, MEDIUM_SMALL, MEDIUM, LARGE)

    @Enumerated(EnumType.STRING)
    private SpacePurpose purpose; // 공간 목적 (INDIVIDUAL, COMMERCIAL, EDUCATION)

    @Enumerated(EnumType.STRING)
    private SpaceMood mood;     // 공간 분위기

    private LocalDate startDate; // 이용시작일
    private LocalDate endDate; // 이용마감일

    private String description; // 공간 설명
    ;

    //운영시간
    private String operatingHours;


    @ElementCollection
    private List<String> imageUrls;
    public String getThumbnailUrl() {
        return (imageUrls != null && !imageUrls.isEmpty())
                ? imageUrls.get(0)
                : null;
    }

    @Enumerated(EnumType.STRING)
    private Status status = Status.APPROVAL_PENDING;

    public enum Status {
        APPROVAL_PENDING,
        APPROVED,
        REJECTED
    }
    @OneToMany(mappedBy = "space", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SpaceLike> spaceLikes;
}