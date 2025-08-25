package com.project.team5backend.domain.image.service.command;

import java.util.List;

public interface ImageCommandService {
    void moveToTrashPrefix(List<String> keys);
}
