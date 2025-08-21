package com.project.team5backend.global.config;

import com.project.team5backend.global.address.exception.AddressErrorCode;
import com.project.team5backend.global.address.exception.AddressException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class KakaoRestClientConfig {

    @Value("${kakao.api.rest-key:}")
    private String restKey;

    @Bean
    public RestClient kakaoRestClient(RestClient.Builder builder) {
        if (restKey == null || restKey.isBlank()) {
            throw new AddressException(AddressErrorCode.MISSING_ADDRESS);
        }
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
}