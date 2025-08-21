// src/main/java/com/project/team5backend/global/address/service/AddressService.java
package com.project.team5backend.global.address.service;

import com.project.team5backend.global.address.client.KakaoLocalClient;
import com.project.team5backend.global.address.converter.AddressConverter;
import com.project.team5backend.global.address.dto.request.AddressReqDTO;
import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.global.address.exception.AddressErrorCode;
import com.project.team5backend.global.address.exception.AddressException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private static final Set<String> SEOUL_GU = Set.of(
            "강남구","강동구","강북구","강서구","관악구","광진구","구로구","금천구","노원구","도봉구",
            "동대문구","동작구","마포구","서대문구","서초구","성동구","성북구","송파구","양천구",
            "영등포구","용산구","은평구","종로구","중구","중랑구"
    );
    private static final Pattern GU_PATTERN = Pattern.compile("([가-힣]+구)");

    private final KakaoLocalClient kakaoLocalClient;

    /**
     * 입력: roadAddress 우선, 없으면 jibunAddress 사용
     * 처리: 카카오 지오코딩 → city/district/neighborhood/lat/lng 채움
     * 제약: "서울특별시" 아닌 경우 거부
     */
    public AddressResDTO.AddressCreateResDTO resolve(AddressReqDTO.AddressCreateReqDTO req) {
        String query = (req.roadAddress() != null && !req.roadAddress().isBlank())
                ? req.roadAddress()
                : req.jibunAddress();

        if (query == null || query.isBlank()) {
            throw new AddressException(AddressErrorCode.ADDRESS_INVALID_INPUT);
        }

        try {
            AddressResDTO.KakaoAddressResDTO res = kakaoLocalClient.searchAddress(query);
            if (res.documents() == null || res.documents().isEmpty()) {
                throw new AddressException(AddressErrorCode.ADDRESS_GEOCODE_NOT_FOUND);
            }

            var doc = res.documents().get(0);

            Double lng = parseDoubleSafe(doc.x()); // x=경도
            Double lat = parseDoubleSafe(doc.y()); // y=위도

            String city = null, district = null, neighborhood = null, roadName = null;

            if (doc.road_address() != null) {
                city = doc.road_address().region_1depth_name();
                district = doc.road_address().region_2depth_name();
                neighborhood = doc.road_address().region_3depth_name();
                roadName = doc.road_address().address_name();
            }
            if (city == null && doc.address() != null) {
                city = doc.address().region_1depth_name();
                district = doc.address().region_2depth_name();
                neighborhood = doc.address().region_3depth_name();
            }

            city = normalizeCity(city);
            district = normalizeDistrict(district, roadName != null ? roadName : query);

            if (!"서울특별시".equals(city)) {
                throw new AddressException(AddressErrorCode.ADDRESS_OUT_OF_REGION);
            }

            return AddressConverter.toCreateAddressResDTO(
                    city, district, neighborhood,
                    roadName, req.detail(), req.postalCode(),
                    lat, lng
            );

        } catch (AddressException e) {
            throw e;
        } catch (Exception e) {
            log.error("[AddressService] 카카오 지오코딩 오류 (query: {})", query, e);
            throw new AddressException(AddressErrorCode.ADDRESS_KAKAO_API_ERROR);
        }
    }

    private static Double parseDoubleSafe(String n) {
        try { return (n == null || n.isBlank()) ? null : Double.parseDouble(n); }
        catch (Exception ignored) { return null; }
    }

    private static String normalizeCity(String raw) {
        if (raw == null) return null;
        raw = raw.trim();
        if (raw.equals("서울") || raw.equals("서울시")) return "서울특별시";
        return raw;
    }

    /**
     * district 없으면 전체 주소 문자열에서 'XX구' 추출 → 서울 25개 구 화이트리스트 검증
     */
    private static String normalizeDistrict(String district, String addressFallback) {
        if (district != null && !district.isBlank()) return district.trim();
        if (addressFallback != null) {
            Matcher m = GU_PATTERN.matcher(addressFallback);
            while (m.find()) {
                String cand = m.group(1);
                if (SEOUL_GU.contains(cand)) return cand;
            }
        }
        return district;
    }
}
