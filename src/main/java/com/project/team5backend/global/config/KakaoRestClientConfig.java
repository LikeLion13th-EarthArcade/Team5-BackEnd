package com.project.team5backend.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class KakaoRestClientConfig {

    @Value("${kakao.api.rest-key:}")
    private String rawRestKey;

    @Bean
    public RestClient kakaoRestClient(RestClient.Builder builder) {
        String restKey = normalizeKey(rawRestKey); // 공백/따옴표 제거

        if (restKey.isBlank()) {
            throw new IllegalStateException(
                    "Missing Kakao REST key. Set 'kakao.api.rest-key' or env KAKAO_API_REST_KEY."
            );
        }
        log.info("[KAKAO] REST key loaded: {}*** (len={})", restKey.substring(0, 4), restKey.length());

        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(java.time.Duration.ofSeconds(2));
        factory.setReadTimeout(java.time.Duration.ofSeconds(3));

        return builder
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(org.springframework.http.HttpHeaders.AUTHORIZATION, "KakaoAK " + restKey)
                .defaultHeader(org.springframework.http.HttpHeaders.ACCEPT, org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
                .requestFactory(factory)
                .build();
    }

    private String normalizeKey(String s) {
        if (s == null) return "";
        // 앞뒤 공백 제거 + 양끝 따옴표 제거
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1);
        }
        return s.trim();
    }
}