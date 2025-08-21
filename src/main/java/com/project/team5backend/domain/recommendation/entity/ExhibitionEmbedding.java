package com.project.team5backend.domain.recommendation.entity;

import com.project.team5backend.global.entity.BaseOnlyCreateTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exhibition_embedding")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ExhibitionEmbedding extends BaseOnlyCreateTimeEntity {

    @Id
    @Column(name = "exhibition_id")
    private Long exhibitionId;

    @Lob
    @Column(name = "vector", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String vector; // "0.01,-0.02,..."

    public float[] toArray() {
        String[] parts = vector.split(",");
        float[] v = new float[parts.length];
        for (int i = 0; i < parts.length; i++) v[i] = Float.parseFloat(parts[i]);
        return v;
    }

    public static String fromArray(float[] v) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < v.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(v[i]);
        }
        return sb.toString();
    }
}