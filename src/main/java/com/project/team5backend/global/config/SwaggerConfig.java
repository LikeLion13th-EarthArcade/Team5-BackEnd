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

        final String COOKIE_SCHEME = "cookieAuth"; // JSESSIONID(세션 사용하는 경우만 의미)

        Components components = new Components()
                .addSecuritySchemes(COOKIE_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.COOKIE)
                        .name("JSESSIONID"));

        // springdoc가 CSRF 헤더를 자동 주입하므로 굳이 xsrfHeader 스키마를 필수로 요구하지 않아도 됨.
        SecurityRequirement security = new SecurityRequirement().addList(COOKIE_SCHEME);

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .addServersItem(new Server().url("https://artiee.store"))
                .info(info)
                .components(components)
                .addSecurityItem(security);
    }
}
