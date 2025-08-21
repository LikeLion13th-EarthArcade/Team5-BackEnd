package com.project.team5backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // 문서 정보
        Info info = new Info()
                .title("Artie API")
                .description("Artie API 명세서")
                .version("1.0.0");

        // 보안 스킴 정의
        final String COOKIE_SCHEME = "cookieAuth";     // 세션 쿠키
        final String XSRF_SCHEME   = "xsrfHeader";     // CSRF 헤더

        Components components = new Components()
                // 세션 쿠키 인증 (기본 세션 쿠키명은 JSESSIONID)
                .addSecuritySchemes(COOKIE_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name("JSESSIONID"))
                // CSRF 헤더 (CookieCsrfTokenRepository 사용 시)
                .addSecuritySchemes(XSRF_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-XSRF-TOKEN"));

        // 기본 보안 요구사항: 세션 + CSRF
        SecurityRequirement security = new SecurityRequirement()
                .addList(COOKIE_SCHEME)
                .addList(XSRF_SCHEME);

        return new OpenAPI()
                // 상대 경로 사용(배포/프록시 환경에서 안전)
                .addServersItem(new Server().url("/"))
                // 필요하면 고정 도메인도 추가(선택)
                .addServersItem(new Server().url("https://artiee.store"))
                .info(info)
                .components(components)
                .addSecurityItem(security);
    }
}
