package org.sejong.sulgamewiki.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode{
  INTERNAL_SERVER_ERROR("서버에 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST),
  POST_NOT_FOUND("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
  MIME_TYPE_NOT_FOUNT("해당하는 타입을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
  private final String message;
  private final HttpStatus status;

}
