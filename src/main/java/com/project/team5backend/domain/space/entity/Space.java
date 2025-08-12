package com.project.team5backend.domain.space.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
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

    private String businessRegistrationNumber;
    private String description;
    private String businessRegistrationDocUrl;
    private String buildingLedgerDocUrl;

    @ElementCollection
    private List<String> imageUrls;

    @Enumerated(EnumType.STRING)
    private Status status = Status.APPROVAL_PENDING;

    public enum Status {
        APPROVAL_PENDING,
        APPROVED,
        REJECTED
    }
}