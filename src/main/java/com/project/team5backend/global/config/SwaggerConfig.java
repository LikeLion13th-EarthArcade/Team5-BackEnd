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
    public OpenAPI LikeLionAPI() {
        Info info = new Info()
                .title("Artie API") // API 제목
                .description("Artie API 명세서 입니다.") // 설명
                .version("1.0.0"); //버전


        // ☑️ 쿠키 인증 스킴 (세션 쿠키 이름에 맞추세요: "SESSION" 또는 "JSESSIONID")
        final String cookieSchemeName = "cookieAuth";

        Components components = new Components()
                .addSecuritySchemes(cookieSchemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)   // OpenAPI에서 쿠키는 apiKey 타입
                                .in(SecurityScheme.In.COOKIE)       // 위치: 쿠키
                                .name("SESSION"));                  // ← 너희가 설정한 쿠키 이름

        return new OpenAPI()
                .addServersItem(new Server().url("https://artiee.store"))
                .info(info)
                // 전체 API에 기본 보안 요구(퍼블릭 엔드포인트는 Controller/@Operation에서 override 가능)
                .addSecurityItem(new SecurityRequirement().addList(cookieSchemeName))
                .components(components);
    }
}