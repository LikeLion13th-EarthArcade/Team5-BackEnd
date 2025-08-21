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
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("SESSION", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE) // 세션 쿠키 사용
                                .name("JSESSIONID")
                        )
                        .addSecuritySchemes("XSRF-TOKEN", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER) // CSRF 헤더
                                .name("X-XSRF-TOKEN")
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList("SESSION")
                        .addList("XSRF-TOKEN")
                );
    }
}