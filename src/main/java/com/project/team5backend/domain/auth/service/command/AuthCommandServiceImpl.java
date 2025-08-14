package com.project.team5backend.domain.auth.service.command;


import com.project.team5backend.domain.user.converter.UserConverter;
import com.project.team5backend.domain.user.dto.request.UserRequest;
import com.project.team5backend.domain.user.dto.response.UserResponse;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, User> sessionStore;
    private final JavaMailSender mailSender;
    private final UserConverter userConverter;


    private final  RedisTemplate<String, String> redisTemplate;

    private static final Duration CODE_EXPIRATION_TIME = Duration.ofMinutes(5);

    private static final String VERIFIED_EMAIL_PREFIX = "verified:";
    // 인증 완료 상태 만료 시간
    private static final Duration VERIFIED_EMAIL_EXPIRATION_TIME = Duration.ofMinutes(10);


    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @Override
    public void sendVerificationCode(String email) {
        String code = generateCode();
        redisTemplate.opsForValue().set(email, code, CODE_EXPIRATION_TIME);
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
        String storedCode = redisTemplate.opsForValue().get(email);
        boolean isValid = code.equals(storedCode);

        if (isValid) {
            // 1. 인증 성공 시, Redis에서 인증 코드 삭제
            redisTemplate.delete(email);

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
        userRepository.save(user);

        // 인증 완료 후 Redis에서 상태 삭제
        redisTemplate.delete(VERIFIED_EMAIL_PREFIX + request.email());
    }


    @Override
    public UserResponse.LoginResult login(UserRequest.Login request, HttpServletResponse response) {
        User user = userRepository.findByEmailAndIsDeletedFalse(request.email())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String sessionId = UUID.randomUUID().toString();
        sessionStore.put(sessionId, user);

        System.out.println("[로그인 성공] SESSION ID: " + sessionId + ", USER ID: " + user.getId() + "가 세션에 저장되었습니다.");

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

        return userConverter.toLoginResult(user);
    }
}
