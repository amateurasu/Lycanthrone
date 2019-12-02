package com.hey.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "user-tracking-Logger")
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class LogUtils {
    public static void log(String format, Object... arguments) {
        log.info(format, arguments);
    }
}
