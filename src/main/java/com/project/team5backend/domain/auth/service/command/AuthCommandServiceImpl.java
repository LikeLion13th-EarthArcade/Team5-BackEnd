package com.project.team5backend.domain.auth.service.command;


import com.project.team5backend.domain.user.dto.request.UserRequest;
import com.project.team5backend.domain.user.dto.response.UserResponse;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, User> sessionStore;


    private final Map<String, String> codeStore = new ConcurrentHashMap<>();


    @Override
    public void sendVerificationCode(String email) {
        String code = UUID.randomUUID().toString().substring(0, 6);
        codeStore.put(email, code);
        System.out.println("[이메일 전송] " + email + " : 인증 코드 = " + code);
    }

    @Override
    @Transactional
    public boolean verifyCode(String email, String code) {
        boolean isValid = code.equals(codeStore.get(email));
        if (isValid) {
            User user = userRepository.findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
            user.setEmailVerified(true);
        }
        return isValid;
    }
    @Override
    public void signUp(UserRequest.SignUp request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    @Override
    public UserResponse.LoginResult login(UserRequest.Login request, HttpServletResponse response) {
        User user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, user);

        System.out.println("[로그인 성공] SESSION ID: " + sessionId + ", USER ID: " + user.getId() + "가 세션에 저장되었습니다.");
        System.out.println("[세션스토어 현재 상태] 크기: " + sessionStore.size() + ", 내용: " + sessionStore);


        ResponseCookie cookie = ResponseCookie.from("SESSION", sessionId)
                .httpOnly(true)
                .secure(false) // 배포 시 true로 변경
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 로그인 성공 후
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new UserResponse.LoginResult(user.getId(), user.getName(), user.getEmail());

    }
}
