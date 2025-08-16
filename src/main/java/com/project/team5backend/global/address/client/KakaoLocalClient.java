package com.project.team5backend.global.address.client;
import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.global.address.exception.AddressErrorCode;
import com.project.team5backend.global.address.exception.AddressException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoLocalClient {

    private final RestClient kakaoRestClient;

    public AddressResDTO.KakaoAddressResDTO searchAddress(String query) {
        return kakaoRestClient.get()
                .uri(u -> u.path("/v2/local/search/address.json")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                // 인증/쿼터 문제는 API 오류로 분리
                .onStatus(status -> status.value() == 401 || status.value() == 403 || status.value() == 429,
                        (req, res) -> {
                            String body = safeBody(res.getBody());
                            log.error("[KAKAO] AUTH/RATE error status={}, body={}", res.getStatusCode(), body);
                            throw new AddressException(AddressErrorCode.ADDRESS_KAKAO_API_ERROR);
                        })
                // 그 외 4xx는 입력 문제로 간주(주소 오타 등) — 원문 로그 남김
                .onStatus(HttpStatusCode::is4xxClientError,
                        (req, res) -> {
                            String body = safeBody(res.getBody());
                            log.warn("[KAKAO] 4xx client error status={}, body={}", res.getStatusCode(), body);
                            throw new AddressException(AddressErrorCode.ADDRESS_INVALID_INPUT);
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {
                            String body = safeBody(res.getBody());
                            log.error("[KAKAO] 5xx server error status={}, body={}", res.getStatusCode(), body);
                            throw new AddressException(AddressErrorCode.ADDRESS_KAKAO_API_ERROR);
                        })
                .body(AddressResDTO.KakaoAddressResDTO.class);
    }

    private String safeBody(InputStream in) {
        try {
            if (in == null) return "(no body)";
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "(failed to read body: " + e.getMessage() + ")";
        }
    }
}