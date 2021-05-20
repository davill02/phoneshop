package com.es.core.cart.exception;

public class PhoneOutOfStockException extends RuntimeException {
    private static final String EXCEPTION_MSG = "We don't have %s in quantity %d";

    public PhoneOutOfStockException(String phoneModel, Long quantity) {
        super(String.format(EXCEPTION_MSG, phoneModel, quantity));
    }
}
