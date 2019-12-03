package com.hey.util;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
// @AllArgsConstructor
public class HeyHttpStatusException extends RuntimeException {
    private final int statusCode;
    private final String code;
    private final String payload;

    public HeyHttpStatusException(int statusCode, String code, String payload) {
        super(HttpResponseStatus.valueOf(statusCode).reasonPhrase(), null, false, false);
        this.code = code;
        this.payload = payload;
        this.statusCode = statusCode;
    }
}
