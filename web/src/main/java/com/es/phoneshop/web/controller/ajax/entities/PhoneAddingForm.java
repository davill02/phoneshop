package com.es.phoneshop.web.controller.ajax.entities;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PhoneAddingForm {
    @NotEmpty
    private Long phoneId;
    @NotEmpty(message = "Quantity is empty")
    @Size(min = 1, max = 4, message = "Quantity can't be more than 4 symbols")
    @Pattern(regexp = "[1-9][0-9]*", message = "Quantity can't be string")
    private String quantity;

    public PhoneAddingForm() {
    }

    public Long getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(Long phoneId) {
        this.phoneId = phoneId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


}
