package com.project.team5backend.global.address.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class AddressException extends CustomException {
  public AddressException(AddressErrorCode errorCode) {
    super(errorCode);
  }
}
