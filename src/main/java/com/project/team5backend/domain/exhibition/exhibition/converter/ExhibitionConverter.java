package com.project.team5backend.domain.exhibition.exhibition.converter;

import com.project.team5backend.domain.exhibition.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.embedded.Address;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionConverter {

    public static Exhibition toEntity (User user, ExhibitionReqDTO.CreateExhibitionReqDTO createReqDTO, String fileKey, Address address) {
        return Exhibition.builder()
                .title(createReqDTO.title())
                .description(createReqDTO.description())
                .startDate(createReqDTO.startDate())
                .endDate(createReqDTO.endDate())
                .openingTime(createReqDTO.openingHour())
                .price(createReqDTO.price())
                .homepageUrl(createReqDTO.homepageUrl())
                .status(Status.PENDING)
                .category(createReqDTO.category())
                .type(createReqDTO.type())
                .mood(createReqDTO.mood())
                .facilities(createReqDTO.facility())
                .isDeleted(false)
                .ratingAvg(BigDecimal.ZERO)
                .likeCount(0)
                .reviewCount(0)
                .thumbnail(fileKey)
                .address(address)
                .user(user)
                .build();
    }

    public static ExhibitionResDTO.PreviewExhibitionResDTO toPreviewExhibitionResDTO (ExhibitionReqDTO.CreateExhibitionReqDTO createReqDTO, List<String> images) {
        return ExhibitionResDTO.PreviewExhibitionResDTO.builder()
                .title(createReqDTO.title())
                .description(createReqDTO.description())
                .startDate(createReqDTO.startDate())
                .endDate(createReqDTO.endDate())
                .openingTime(createReqDTO.openingHour())
                .imageUrls(images)
                .homepageUrl(createReqDTO.homepageUrl())
                .price(createReqDTO.price())
                .address(createReqDTO.address().roadAddress() + createReqDTO.address().detail())
                .category(createReqDTO.category())
                .type(createReqDTO.type())
                .mood(createReqDTO.mood())
                .facility(createReqDTO.facility())
                .build();
    }

    public static ExhibitionResDTO.DetailExhibitionResDTO toDetailExhibitionResDTO(Exhibition exhibition, List<String> imageFileKeys) {
        return ExhibitionResDTO.DetailExhibitionResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .startDate(exhibition.getStartDate())
                .endDate(exhibition.getEndDate())
                .openingTime(exhibition.getOpeningTime())
                .imageFileKeys(imageFileKeys)
                .homepageUrl(exhibition.getHomepageUrl())
                .address(
                        exhibition.getAddress() != null ? exhibition.getAddress().toString() : null
                )
                .category(exhibition.getCategory())
                .type(exhibition.getType())
                .mood(exhibition.getMood())
                .price(exhibition.getPrice())
                .facility(exhibition.getFacilities())
                .reviews(null)
                .build();
    }

    public static ExhibitionResDTO.SearchExhibitionResDTO toSearchExhibitionResDTO(Exhibition exhibition) {
        return ExhibitionResDTO.SearchExhibitionResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .thumbnail(exhibition.getThumbnail())
                .startDate(exhibition.getStartDate())
                .endDate(exhibition.getEndDate())
                .address(exhibition.getAddress().getRoadAddress() + exhibition.getAddress().getDetail())
                .latitude(Double.valueOf(exhibition.getAddress().getLatitude()))
                .longitude(Double.valueOf(exhibition.getAddress().getLongitude()))
                .build();
    }
    public static ExhibitionResDTO.SearchExhibitionPageResDTO toSearchExhibitionPageResDTO(
            List<ExhibitionResDTO.SearchExhibitionResDTO> items,
            Page<?> page,
            Double defaultCenterLat,
            Double defaultCenterLng) {

        // PageInfo 생성
        ExhibitionResDTO.SearchExhibitionPageResDTO.PageInfo pageInfo =
                new ExhibitionResDTO.SearchExhibitionPageResDTO.PageInfo(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isFirst(),
                        page.isLast()
                );

        // MapInfo 생성
        ExhibitionResDTO.SearchExhibitionPageResDTO.MapInfo mapInfo =
                new ExhibitionResDTO.SearchExhibitionPageResDTO.MapInfo(
                        defaultCenterLat,
                        defaultCenterLng
                );

        return new ExhibitionResDTO.SearchExhibitionPageResDTO(items, pageInfo, mapInfo);
    }

    public static ExhibitionResDTO.HotNowExhibitionResDTO toHotNowExhibitionResDTO(Long exhibitionId, String title, List<String> fileKeys) {
        return ExhibitionResDTO.HotNowExhibitionResDTO.builder()
                .exhibitionId(exhibitionId)
                .title(title)
                .images(fileKeys)
                .build();
    }

    public static ExhibitionResDTO.UpcomingPopularityExhibitionResDTO toUpcomingPopularityExhibitionResDTO(
            Long exhibitionId, String title, List<String> fileKeys
    ){
        return ExhibitionResDTO.UpcomingPopularityExhibitionResDTO.builder()
                .exhibitionId(exhibitionId)
                .title(title)
                .images(fileKeys)
                .build();
    }

    public static ExhibitionResDTO.PopularRegionExhibitionListResDTO toPopularRegionExhibitionListResDTO(List<ExhibitionResDTO.PopularRegionExhibitionResDTO> exhibitions) {
        return ExhibitionResDTO.PopularRegionExhibitionListResDTO.builder()
                .exhibitions(exhibitions)
                .build();
    }

    public static ExhibitionResDTO.PopularRegionExhibitionResDTO toPopularRegionExhibitionResDTO(Exhibition exhibition) {
        return ExhibitionResDTO.PopularRegionExhibitionResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .thumbnail(exhibition.getThumbnail())
                .build();
    }

    public static ExhibitionResDTO.ArtieRecommendationResDTO toArtieRecommendationResDTO(Exhibition exhibition) {
        return ExhibitionResDTO.ArtieRecommendationResDTO.builder()
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .thumbnail(exhibition.getThumbnail())
                .build();
    }
}
