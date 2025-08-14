package com.project.team5backend.domain.space.space.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@Setter
@Builder
@Table(name = "Space")
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    @Enumerated(EnumType.STRING)
    private SpaceType type;
    private String spec;

    @Enumerated(EnumType.STRING)
    private SpacePurpose purpose;

    @Enumerated(EnumType.STRING)
    private SpaceMood mood;

    private LocalDate startDate;
    private LocalDate endDate;

    // 누가 공간을 등록했냐
    private String submittedBy;

    // 언제 공간을 등록했냐
    @CreatedDate
    private LocalDateTime submittedAt;

    //운영시간
    private String operatingHours;



    private String businessRegistrationNumber;
    private String description;
    private String businessRegistrationDocUrl;
    private String buildingLedgerDocUrl;

    @ElementCollection
    private List<String> imageUrls;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.APPROVAL_PENDING;

    public enum Status {
        APPROVAL_PENDING,
        APPROVED,
        REJECTED
    }
}