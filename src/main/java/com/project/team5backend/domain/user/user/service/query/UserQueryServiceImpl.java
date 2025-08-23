package com.project.team5backend.domain.user.user.service.query;

import com.project.team5backend.domain.user.user.converter.UserConverter;
import com.project.team5backend.domain.user.user.dto.response.UserResponse;
import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Override
    public UserResponse.MyInfo getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return userConverter.toMyInfo(user);
    }
}

