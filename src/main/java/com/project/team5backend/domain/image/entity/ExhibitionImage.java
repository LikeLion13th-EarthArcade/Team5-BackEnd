package com.project.team5backend.domain.image.entity;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.global.entity.BaseOnlyCreateTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExhibitionImage extends BaseOnlyCreateTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_image_id")
    private Long id;

    @Column(name = "image_url")
    private String fileKey;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_id")
    private Exhibition exhibition;

    public void deleteImage() {
        isDeleted = true;
        deletedAt = LocalDateTime.now();
    }
}
