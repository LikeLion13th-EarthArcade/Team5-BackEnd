package com.project.team5backend.domain.user.user.service.command;

import com.project.team5backend.domain.user.user.dto.request.UserRequest;
import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void updateUserInfo(Long userId, UserRequest.UpdateUserInfo request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이름 변경 로직: newName 값이 있을 경우에만 실행
        if (request.newName() != null) {
            user.changeName(request.newName());
        }

        // 비밀번호 변경 로직: newPassword 값이 있을 경우에만 실행
        if (request.newPassword() != null) {
            // 필수 값 검증
            if (request.currentPassword() == null || request.newPasswordConfirmation() == null) {
                throw new IllegalArgumentException("비밀번호 변경 시 필수 입력 값이 누락되었습니다.");
            }

            // 1. 현재 비밀번호 일치 여부 확인
            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }

            // 2. 새 비밀번호와 새 비밀번호 확인 일치 여부 확인
            if (!request.newPassword().equals(request.newPasswordConfirmation())) {
                throw new IllegalArgumentException("새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
            }

            // 3. 비밀번호 변경
            user.changePassword(passwordEncoder.encode(request.newPassword()));
        }

        userRepository.save(user); // 한 번의 save 호출로 변경된 내용 모두 반영
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        //user.setDeleted(true); -> softDelete 쓸거면 이거 2개 써라
        //userRepository.save(user);

        userRepository.delete(user);
    }

}