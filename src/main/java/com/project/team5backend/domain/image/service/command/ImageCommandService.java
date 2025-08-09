package com.project.team5backend.domain.image.service.command;

import com.project.team5backend.domain.image.dto.request.ImageReqDTO;
import com.project.team5backend.domain.image.dto.response.ImageResDTO;

import java.util.List;

public interface ImageCommandService {
    ImageResDTO.PresignedUrlResDTO generatePresignedUrl(String email, ImageReqDTO.PresignedUrlReqDTO presignedUrl);

    ImageResDTO.DeleteImageResDTO delete(String email, String fileKey);

    void moveToTrashPrefix(List<String> keys);

    void clearTrackingByEmail(String email);
}
