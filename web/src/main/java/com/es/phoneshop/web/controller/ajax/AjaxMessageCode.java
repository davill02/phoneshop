package com.es.phoneshop.web.controller.ajax;

public enum AjaxMessageCode {
    SUCCESS(1), ERROR(0);
    int code;

    AjaxMessageCode(int code) {
        this.code = code;
    }
}
