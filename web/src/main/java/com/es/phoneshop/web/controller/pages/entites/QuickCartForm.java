package com.es.phoneshop.web.controller.pages.entites;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class QuickCartForm {
    @Valid
    List<AddingPhoneRow> rows;

    public QuickCartForm() {
        this.rows = new ArrayList<>(10);
    }

    public List<AddingPhoneRow> getRows() {
        return rows;
    }

    public void setRows(List<AddingPhoneRow> rows) {
        this.rows = rows;
    }
}
