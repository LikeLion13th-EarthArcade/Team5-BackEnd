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
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> codeStore = new ConcurrentHashMap<>();


    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Override
    public void sendVerificationCode(String email) {
        String code = UUID.randomUUID().toString().substring(0, 6);
        codeStore.put(email, code);
        // TODO: 실제 이메일 전송 로직 넣기
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
        // 개발 완료되면 뺴도 됨
        System.out.println("✅ user = " + user);
        System.out.println("✅ id = " + user.getId());
        System.out.println("✅ name = " + user.getName());
        System.out.println("✅ email = " + user.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String sessionId = UUID.randomUUID().toString();

        ResponseCookie cookie = ResponseCookie.from("SESSION", sessionId)
                .httpOnly(true)
                .secure(false) // 배포 시 true로 변경
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new UserResponse.LoginResult(user.getId(), user.getName(), user.getEmail());

    }

    @Override
    public UserResponse.LoginResult kakaoLogin(String code, HttpServletResponse response) {
        String tokenUri = "https://kauth.kakao.com/oauth/token";
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoRestApiKey);
        body.add("redirect_uri", kakaoRedirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUri, tokenRequest, Map.class);
        String accessToken = (String) tokenResponse.getBody().get("access_token");

        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, userInfoRequest, Map.class);

        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfoResponse.getBody().get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(nickname)
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .isEmailVerified(true)
                            .build();
                    return userRepository.save(newUser);
                });

        CookieUtil.addCookie(response, "USER_ID", String.valueOf(user.getId()), 60 * 60 * 24);
        return new UserResponse.LoginResult(user.getId(), user.getName(), user.getEmail());
    }
}
