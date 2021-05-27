package com.es.phoneshop.web.controller.pages.entites;

import java.util.ArrayList;
import java.util.List;

public class QuickCartAddingForm {
    private List<String> model;
    private List<String> quantity;

    public QuickCartAddingForm() {
        model = new ArrayList<>(10);
        quantity = new ArrayList<>(10);
    }

    public List<String> getModel() {
        return model;
    }

    public void setModel(List<String> model) {
        this.model = model;
    }

    public List<String> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<String> quantity) {
        this.quantity = quantity;
    }
}
