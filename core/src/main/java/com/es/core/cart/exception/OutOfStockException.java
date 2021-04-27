package com.es.core.cart.exception;

public class OutOfStockException extends RuntimeException {
    private static final String EXCEPTION_MSG = "We don't have %s in quantity %d";

    public OutOfStockException(String phoneModel, Long quantity) {
        super(String.format(EXCEPTION_MSG, phoneModel, quantity));
    }
}
