package com.es.core.order;

import java.util.List;

public class OutOfStockException extends Exception {
    private String userMessage;
    private List<Long> phoneIds;

    public OutOfStockException(String userMessage, List<Long> phoneIds) {
        this.userMessage = userMessage;
        this.phoneIds = phoneIds;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public List<Long> getPhoneIds() {
        return phoneIds;
    }

    public void setPhoneIds(List<Long> phoneIds) {
        this.phoneIds = phoneIds;
    }
}
