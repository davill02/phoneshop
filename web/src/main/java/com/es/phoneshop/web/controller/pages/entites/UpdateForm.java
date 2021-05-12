package com.es.phoneshop.web.controller.pages.entites;

import java.util.List;

public class UpdateForm {
    private List<Long> phoneId;
    private List<String> quantity;

    public UpdateForm() {
    }

    public List<Long> getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(List<Long> phoneId) {
        this.phoneId = phoneId;
    }

    public List<String> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<String> quantity) {
        this.quantity = quantity;
    }
}
