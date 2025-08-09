package com.project.team5backend.domain.exhibition.exhibition.converter;

import com.project.team5backend.domain.exhibition.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Status;
import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionConverter {

    public static Exhibition toEntity (User user, ExhibitionReqDTO.CreateExhibitionReqDTO createReqDTO, String fileKey) {
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
                .ratingAvg(0)
                .likeCount(0)
                .reviewCount(0)
                .thumbnail(fileKey)
                .address(null)
                .user(user)
                .build();
    }

    public static ExhibitionResDTO.PreviewExhibitionResDTO toPreviewExhibitionResDTO (ExhibitionReqDTO.CreateExhibitionReqDTO createReqDTO) {
        return ExhibitionResDTO.PreviewExhibitionResDTO.builder()
                .title(createReqDTO.title())
                .description(createReqDTO.description())
                .startDate(createReqDTO.startDate())
                .endDate(createReqDTO.endDate())
                .openingTime(createReqDTO.openingHour())
                .imageUrls(null)
                .homepageUrl(createReqDTO.homepageUrl())
                .price(createReqDTO.price())
                .address(createReqDTO.address())
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
                .reviews(exhibition.getReviews().stream()
                        .map(ExhibitionConverter::toReviewPreview)
                        .toList())
                .build();
    }

    private static ExhibitionResDTO.ExhibitionReviewPreviewResDTO toReviewPreview(ExhibitionReview review) {
        return ExhibitionResDTO.ExhibitionReviewPreviewResDTO.builder()
                .reviewId(review.getId())
                .name(review.getUser().getName())
                .content(review.getContent())
                .imageUrls(null)
                .createdAt(review.getCreateAt().toLocalDate())
                .build();

    }
}
