package com.project.team5backend.domain.user.service.command;

import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, User> sessionStore;

    // 세션 스토어에서 사용자를 찾아 최신 정보로 갱신하는 보조 메서드
    private void updateSessionStore(User updatedUser) {
        // userId에 해당하는 sessionId를 찾음
        Optional<String> optionalSessionId = sessionStore.entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(updatedUser.getId()))
                .map(Map.Entry::getKey)
                .findFirst();

        // sessionId가 존재하면, 해당 키의 value(User 객체)를 최신 정보로 교체
        optionalSessionId.ifPresent(sessionId -> sessionStore.put(sessionId, updatedUser));
    }

    @Override
    public void updateName(Long userId, String newName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.changeName(newName);

        User updatedUser = userRepository.save(user); // db 업데이트
        updateSessionStore(updatedUser); // 세션 스토어 업데이트
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        user.changePassword(passwordEncoder.encode(newPassword));

        User updatedUser = userRepository.save(user); // db 업데이트
        updateSessionStore(updatedUser); // 세션 스토어 업데이트
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

}