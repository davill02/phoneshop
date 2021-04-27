package com.es.core.cart.exception;

public class IllegalPhoneException extends RuntimeException {
    private final static String EXCEPTION_MSG = "Phone with id %d doesn't exist or doesn't have stock";
    private final static String EXCEPTION_PRICE_IS_NULL_MSG = "Phone %s doesn't have price";
    public final static String EXCEPTION_ILLEGAL_PHONE = "Illegal phone";

    public IllegalPhoneException() {
        super(EXCEPTION_ILLEGAL_PHONE);
    }

    public IllegalPhoneException(Long id) {
        super(String.format(EXCEPTION_MSG, id));
    }

    public IllegalPhoneException(String model) {
        super(String.format(EXCEPTION_PRICE_IS_NULL_MSG, model));
    }
}
