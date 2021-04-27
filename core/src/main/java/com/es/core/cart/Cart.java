package com.es.core.cart;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cart {
    private Map<Long, Long> cart;
    private Long quantity = 0L;
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public Cart() {
        this.cart = new ConcurrentHashMap<>();
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

    public Map<Long, Long> getCart() {
        return cart;
    }

    public void setCart(Map<Long, Long> cart) {
        this.cart = cart;
    }
}
