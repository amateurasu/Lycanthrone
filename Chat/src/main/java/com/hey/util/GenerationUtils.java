package com.hey.util;

import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GenerationUtils {
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
