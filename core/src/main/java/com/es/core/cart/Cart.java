package com.es.core.cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;
    private Long quantity = 0L;
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public Cart() {
        items = new ArrayList<>();
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
