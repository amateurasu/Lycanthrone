package com.hey.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "user-tracking-Logger")
public final class LogUtils {
    private LogUtils() {
    }

    public static void log(String format, Object... arguments) {
        log.info(format, arguments);
    }
}
