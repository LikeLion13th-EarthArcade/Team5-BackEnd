package com.project.team5backend.domain.user.converter;

import com.project.team5backend.domain.user.dto.request.UserRequest;
import com.project.team5backend.domain.user.dto.response.UserResponse;
import com.project.team5backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;


    // SignUp DTO를 User 엔티티로 변환
    public User toUser(UserRequest.SignUp request) {
        boolean verified = "true".equals(redisTemplate.opsForValue().get("verified:" + request.email()));
        return User.builder()
                .email(request.email())
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .isEmailVerified(verified) // 이메일 인증이 완료된 상태로 가정
                .build();
    }

    // User 엔티티를 LoginResult DTO로 변환
    public UserResponse.LoginResult toLoginResult(User user) {
        return new UserResponse.LoginResult(user.getId(), user.getName(), user.getEmail());
    }

    // User 엔티티를 MyInfo DTO로 변환
    public UserResponse.MyInfo toMyInfo(User user) {
        return new UserResponse.MyInfo(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
    }
}