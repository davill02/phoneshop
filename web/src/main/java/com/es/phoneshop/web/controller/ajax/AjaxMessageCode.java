package com.es.phoneshop.web.controller.ajax;

public enum AjaxMessageCode {
    SUCCESS(1), ERROR(0);
    private int code;

    public int getCode() {
        return code;
    }

    AjaxMessageCode(int code) {
        this.code = code;
    }
}
