package com.hey.util;

@lombok.AllArgsConstructor
public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    INTERNAL_SERVER_ERROR(500, " Internal Server Error");

    private int code;
    private String message;

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
