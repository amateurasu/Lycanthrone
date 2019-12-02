package com.hey.util;

import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class GenerationUtils {
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
