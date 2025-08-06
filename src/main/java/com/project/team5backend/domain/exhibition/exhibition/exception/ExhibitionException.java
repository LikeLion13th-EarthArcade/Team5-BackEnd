package com.project.team5backend.domain.exhibition.exhibition.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class ExhibitionException extends CustomException {
  public ExhibitionException(ExhibitionErrorCode errorCode) {
    super(errorCode);
  }
}
