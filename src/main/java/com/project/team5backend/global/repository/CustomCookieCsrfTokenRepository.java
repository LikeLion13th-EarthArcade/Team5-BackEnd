package com.project.team5backend.global.repository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

public class CustomCookieCsrfTokenRepository implements CsrfTokenRepository {

    private final CookieCsrfTokenRepository delegate = CookieCsrfTokenRepository.withHttpOnlyFalse();

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return delegate.generateToken(request);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (token == null) {
            // 토큰이 null이면 쿠키 삭제
            response.addHeader("Set-Cookie", "XSRF-TOKEN=; Path=/; Max-Age=0; Secure; SameSite=None");
            return;
        }

        // delegate 호출하지 않고 직접 쿠키 설정
        response.addHeader("Set-Cookie",
                String.format("XSRF-TOKEN=%s; Path=/; Secure; SameSite=None; HttpOnly=false",
                        token.getToken()));
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return delegate.loadToken(request);
    }
}
