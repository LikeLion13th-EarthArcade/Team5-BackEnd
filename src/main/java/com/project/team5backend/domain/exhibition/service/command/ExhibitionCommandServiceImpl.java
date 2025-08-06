package com.project.team5backend.domain.exhibition.service.command;

import com.project.team5backend.domain.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionCommandServiceImpl implements ExhibitionCommandService {

    private final ExhibitionRepository exhibitionRepository;
    private final UserRepository userRepository;

    @Override
    public void createExhibition(ExhibitionReqDTO.CreateExhibitionReqDTO createExhibitionReqDTO) {
        User user = new User();
        userRepository.save(user);
        Exhibition ex = ExhibitionConverter.toEntity(user, createExhibitionReqDTO);

        exhibitionRepository.save(ex);
    }
}
