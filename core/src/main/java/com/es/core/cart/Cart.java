package com.es.core.cart;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cart {
    private Map<Long, Long> productId2Quantity;
    private Long quantity = 0L;
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public Cart() {
        this.productId2Quantity = new ConcurrentHashMap<>();
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

    public Map<Long, Long> getProductId2Quantity() {
        return productId2Quantity;
    }

    public void setProductId2Quantity(Map<Long, Long> productId2Quantity) {
        this.productId2Quantity = productId2Quantity;
    }
}
