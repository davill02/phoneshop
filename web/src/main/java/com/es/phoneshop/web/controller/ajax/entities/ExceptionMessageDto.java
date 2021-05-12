package com.es.phoneshop.web.controller.ajax.entities;

public class ExceptionMessageDto {
    private int code;
    private String message;

    public ExceptionMessageDto() {
    }

    public ExceptionMessageDto(int code, String message) {
        this.code = code;
        this.message = message;
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
}
