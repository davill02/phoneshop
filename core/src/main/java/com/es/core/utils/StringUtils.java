package com.es.core.utils;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean isBlank(String string) {
        if (string == null) {
            return true;
        }
        if (string.trim().isEmpty()) {
            return true;
        }
        return false;
    }
}
