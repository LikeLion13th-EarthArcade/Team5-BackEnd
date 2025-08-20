package com.project.team5backend.domain.recommendation.entity;

import com.project.team5backend.domain.recommendation.model.ActionType;
import com.project.team5backend.global.entity.BaseOnlyCreateTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exhibition_interact_log")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionInteractLog extends BaseOnlyCreateTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="exhibition_id", nullable=false)
    private Long exhibitionId;

    @Enumerated(EnumType.STRING)
    @Column(name="action_type", nullable=false)
    private ActionType actionType;
}
