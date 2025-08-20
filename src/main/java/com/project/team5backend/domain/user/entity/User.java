package com.project.team5backend.domain.user.entity;

import lombok.*;
import com.project.team5backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "is_email_verified")
    private boolean isEmailVerified = false;

    public void verifyEmail() {
        this.isEmailVerified = true;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void delete() {
        this.isDeleted = true;
    }

    @Enumerated(EnumType.STRING)
    private Role role;
}
