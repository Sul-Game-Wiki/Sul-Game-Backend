package org.sejong.sulgamewiki.admin.exception;

import static org.sejong.sulgamewiki.common.exception.constants.ErrorSource.ADMIN;

import org.sejong.sulgamewiki.common.exception.CustomException;
import org.sejong.sulgamewiki.common.exception.ErrorCode;

public class AdminException extends CustomException {

    public AdminException(ErrorCode errorCode) {
        super(errorCode, ADMIN.name());
    }
}