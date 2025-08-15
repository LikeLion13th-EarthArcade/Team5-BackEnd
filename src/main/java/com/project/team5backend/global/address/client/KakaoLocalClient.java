package com.project.team5backend.global.address.client;
import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.global.address.exception.AddressErrorCode;
import com.project.team5backend.global.address.exception.AddressException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KakaoLocalClient {

    private final RestClient kakaoRestClient;

    public AddressResDTO.KakaoAddressResDTO searchAddress(String query) {
        return kakaoRestClient.get()
                .uri(u -> u.path("/v2/local/search/address.json").queryParam("query", query).build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new AddressException(AddressErrorCode.ADDRESS_KAKAO_API_ERROR);
                })
                .body(AddressResDTO.KakaoAddressResDTO.class);
    }
}