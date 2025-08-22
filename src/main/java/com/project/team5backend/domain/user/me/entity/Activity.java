package com.project.team5backend.domain.user.me.entity;


import com.project.team5backend.domain.user.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ActivityId;

    private String ActivityType; // "EXHIBITION_LIKE", "SPACE_LIKE", "REVIEW"

    private String title; // 좋아요한 전시/공간 이름 or 리뷰 제목

    @Column(length = 1000)
    private String content; // 리뷰 내용이면 여기에 저장

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
