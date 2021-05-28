package com.es.phoneshop.web.controller.pages.entites;

import com.es.phoneshop.web.controller.pages.validators.annotations.ValidQuantityAndExistedModel;

import javax.validation.constraints.Min;


@ValidQuantityAndExistedModel
public class AddingPhoneRow {
    private String model;
    @Min(value = 1, message = "Quantity should be positive")
    private Long quantity;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
