package com.project.team5backend.global.address.client;
import com.project.team5backend.global.address.dto.response.AddressResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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
                .body(AddressResDTO.KakaoAddressResDTO.class);
    }
}