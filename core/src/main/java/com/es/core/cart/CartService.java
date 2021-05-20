package com.es.core.cart;

import java.util.Map;

public interface CartService {

    void addPhone(Long phoneId, Long quantity, Cart cart);

    /**
     * @param items key: phoneId
     *              value: quantity
     */
    void update(Map<Long, Long> items, Cart cart);

    void remove(Long phoneId, Cart cart);

    void clear(Cart cart);
}
