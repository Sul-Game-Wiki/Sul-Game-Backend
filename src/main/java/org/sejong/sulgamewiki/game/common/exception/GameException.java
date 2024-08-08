package org.sejong.sulgamewiki.game.common.exception;


import static org.sejong.sulgamewiki.common.exception.constants.ErrorSource.GAME;

import org.sejong.sulgamewiki.common.exception.CustomException;
import org.sejong.sulgamewiki.common.exception.ErrorCode;

public class GameException extends CustomException {

  public GameException(ErrorCode errorCode) {
    super(errorCode, GAME.name());
  }
}
