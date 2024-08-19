package org.sejong.sulgamewiki.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

  private ErrorCode errorCode;
  private String errorMessage;
}