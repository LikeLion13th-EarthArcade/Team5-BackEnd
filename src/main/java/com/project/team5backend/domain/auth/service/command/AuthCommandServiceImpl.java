package com.project.team5backend.domain.auth.service.command;


import com.project.team5backend.domain.user.user.converter.UserConverter;
import com.project.team5backend.domain.user.user.dto.request.UserRequest;
import com.project.team5backend.domain.user.user.dto.response.UserResponse;
import com.project.team5backend.domain.user.user.entity.Role;
import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import com.project.team5backend.global.apiPayload.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.time.Duration;
import java.util.Random;
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final UserConverter userConverter;
    private final StringRedisTemplate redisTemplate;

    private static final String CODE_PREFIX = "email:code:";

    private static final Duration CODE_EXPIRATION_TIME = Duration.ofMinutes(5);

    private static final String VERIFIED_EMAIL_PREFIX = "verified:";
    // 인증 완료 상태 만료 시간
    private static final Duration VERIFIED_EMAIL_EXPIRATION_TIME = Duration.ofMinutes(10);
    private final AuthenticationManager authenticationManager;


    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @Override
    public void sendVerificationCode(String email) {
        String code = generateCode();
        redisTemplate.opsForValue().set(CODE_PREFIX + email, code, CODE_EXPIRATION_TIME);

        System.out.println("[이메일 전송] " + email + " : 인증 코드 = " + code);
        //  실제 이메일 전송 로직 구현
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("회원가입 인증 코드입니다.");
        message.setText("인증 코드는 " + code + " 입니다.");
        mailSender.send(message);
    }
    @Override
    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(CODE_PREFIX + email);
        boolean isValid = code.equals(storedCode);

        if (isValid) {
            // 1. 인증 성공 시, Redis에서 인증 코드 삭제
            redisTemplate.delete(CODE_PREFIX + email); // ✅

            // 2. Redis에 이메일 인증 완료 상태 저장
            redisTemplate.opsForValue().set(VERIFIED_EMAIL_PREFIX + email, "true", VERIFIED_EMAIL_EXPIRATION_TIME);
            //user.verifyEmail();
        }
        return isValid;
    }
    @Override
    public void signUp(UserRequest.SignUp request) {
        String verifiedStatus = redisTemplate.opsForValue().get(VERIFIED_EMAIL_PREFIX + request.email());

        // Redis에서 이메일 인증 완료 상태 확인
        if (verifiedStatus == null || !verifiedStatus.equals("true")) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // UserConverter를 사용해 DTO를 엔티티로 변환
        User user = userConverter.toUser(request);
        user.setRole(Role.USER);
        userRepository.save(user);

        // 인증 완료 후 Redis에서 상태 삭제
        redisTemplate.delete(VERIFIED_EMAIL_PREFIX + request.email());
    }

    @Override
    public UserResponse.LoginResult login(UserRequest.Login request,
                                          HttpServletRequest httpRequest,
                                          HttpServletResponse httpResponse) {

        // 1. AuthenticationManager로 표준 인증
        Authentication authRequest =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(authRequest);
        // 2. SecurityContext 생성 및 세션에 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        HttpSession oldSession = httpRequest.getSession(false);
        if (oldSession != null) oldSession.invalidate();
        // 새 세션 생성

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        session.setMaxInactiveInterval(30 * 60); // 30분

        //  로그인 결과 반환
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 조회 실패"));

        return new UserResponse.LoginResult(user.getId(), user.getName(), "로그인 성공");
    }
    @Override
    public void logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }
        SecurityContextHolder.clearContext(); // SecurityContext 정리
    }

}
