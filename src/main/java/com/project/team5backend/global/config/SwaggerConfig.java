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
        Info info = new Info()
                .title("Artie API")
                .description("Artie API 명세서")
                .version("1.0.0");

        final String COOKIE_SCHEME = "cookieAuth";   // JSESSIONID (자동 전송)
        final String XSRF_SCHEME   = "xsrfHeader";   // X-XSRF-TOKEN (헤더로 보냄)

        Components components = new Components()
                // 세션 쿠키
                .addSecuritySchemes(COOKIE_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name("JSESSIONID"))
                // CSRF 헤더
                .addSecuritySchemes(XSRF_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-XSRF-TOKEN"));

        // 기본 요구: 세션 + CSRF 헤더
        SecurityRequirement security = new SecurityRequirement()
                .addList(COOKIE_SCHEME)
                .addList(XSRF_SCHEME);

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .addServersItem(new Server().url("https://artiee.store"))
                .info(info)
                .components(components)
                .addSecurityItem(security); // ✅ 하나만 추가
    }
}
