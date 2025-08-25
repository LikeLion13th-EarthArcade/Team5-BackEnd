package com.project.team5backend.domain.space.space.service.command;


import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.image.converter.ImageConverter;
import com.project.team5backend.domain.image.exception.ImageErrorCode;
import com.project.team5backend.domain.image.exception.ImageException;
import com.project.team5backend.domain.image.service.RedisImageTracker;
import com.project.team5backend.domain.space.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.space.dto.request.SpaceRequest;
import com.project.team5backend.domain.space.space.dto.response.SpaceResponse;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.entity.SpaceLike;
import com.project.team5backend.domain.space.space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;

import com.project.team5backend.domain.user.user.entity.User;
import com.project.team5backend.domain.user.user.repository.UserRepository;
import com.project.team5backend.global.address.converter.AddressConverter;
import com.project.team5backend.global.address.dto.response.AddressResDTO;
import com.project.team5backend.global.address.service.AddressService;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.project.team5backend.domain.user.user.entity.QUser.user;

@Service
@RequiredArgsConstructor
@Transactional
public class SpaceCommandServiceImpl implements SpaceCommandService {

    private final SpaceRepository spaceRepository;
    private final SpaceLikeRepository spaceLikeRepository;
    private final SpaceConverter spaceConverter;
    private final UserRepository userRepository;
    private final RedisImageTracker redisImageTracker;
    private final SpaceImageRepository spaceImageRepository;
    private final AddressService addressService;
    private final S3Uploader s3Uploader;

    @Override
    public SpaceResponse.SpaceRegistrationResponse registerSpace(SpaceRequest.Create request, String email, List<MultipartFile> images) {


        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (images.isEmpty()) throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
        //주소 가져오기
        AddressResDTO.AddressCreateResDTO addressResDTO = addressService.resolve(request.address());
        Address address = AddressConverter.toAddress(addressResDTO);
        // 3. Space 엔티티 생성 (대표 이미지: 첫 번째 파일 key)
        // 업로드 및 fileKey 획득
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : images) {
            String url = s3Uploader.upload(file, "spaces");
            imageUrls.add(url);
        }

        Space space = spaceConverter.toSpace(request, user, imageUrls.get(0),address);
        Space savedSpace = spaceRepository.save(space);

        // 4. Space 이미지 엔티티 저장
        for (String fileKey : imageUrls) {
            spaceImageRepository.save(ImageConverter.toEntitySpaceImage(savedSpace, fileKey));
        }
        return spaceConverter.toSpaceRegistrationResponse(savedSpace);
    }

    @Override
    public boolean toggleLike(Long spaceId, Long userId) {
        // ... 기존 로직과 동일
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 전시 공간이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
        return spaceLikeRepository.findBySpaceIdAndUserId(spaceId, userId)
                .map(existingLike -> {
                    spaceLikeRepository.delete(existingLike);
                    return false;
                })
                .orElseGet(() -> {
                    SpaceLike like = new SpaceLike();
                    like.setSpace(space);
                    like.setUser(user);
                    spaceLikeRepository.save(like);
                    return true;
                });
    }

    @Override
    public void deleteSpace(Long spaceId) {
        spaceRepository.deleteById(spaceId);
    }

    @Override
    public void approveSpace(Long spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found"));
        space.setStatus(Space.Status.APPROVED);
    }
}