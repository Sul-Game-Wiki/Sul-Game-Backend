package org.sejong.sulgamewiki.common.like.exception;

import static org.sejong.sulgamewiki.common.exception.constants.ErrorSource.LIKE;

import lombok.Getter;
import org.sejong.sulgamewiki.common.exception.CustomException;
import org.sejong.sulgamewiki.common.exception.constants.ErrorSource;

@Getter
public class LikeException extends CustomException {
  public LikeException(LikeErrorCode errorCode) {
    super(errorCode, LIKE.name());
  }
}
