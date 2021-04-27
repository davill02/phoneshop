package com.es.phoneshop.web.controller.ajax;

import java.math.BigDecimal;

public class AjaxMessage {
    private int code;
    private String message;
    private BigDecimal totalPrice;
    private Long quantity;

    public AjaxMessage() {
    }

    public AjaxMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public AjaxMessage(int code, String message, BigDecimal totalPrice, Long quantity) {
        this.code = code;
        this.message = message;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
