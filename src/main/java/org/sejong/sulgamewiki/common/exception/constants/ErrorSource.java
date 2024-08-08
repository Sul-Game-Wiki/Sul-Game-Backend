package org.sejong.sulgamewiki.common.exception.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorSource {
    GAME,
    INTRO,
    NOTI,
    MEMBER,
    ADMIN,
    LIKE,
    OTHERS;
}