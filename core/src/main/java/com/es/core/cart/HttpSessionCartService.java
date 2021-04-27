package com.es.core.cart;

import com.es.core.cart.exception.IllegalPhoneException;
import com.es.core.cart.exception.OutOfStockException;
import com.es.core.model.phone.PhoneDao;
import com.es.core.model.phone.Stock;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class HttpSessionCartService implements CartService {

    private static final String QUANTITY_IS_NULL_EXCEPTION_MSG = "Quantity is null";
    private static final String CART_IS_NULL_EXCEPTION_MSG = "Cart is null";
    private static final String QUANTITY_IS_NEGATIVE_EXCEPTION_MESSAGE = "Quantity can't be negative";
    @Resource
    private PhoneDao phoneDao;

    @Override
    public Cart getCart(Cart cart) {
        if (cart == null) {
            cart = new Cart();
        }
        return cart;
    }

    @Override
    public void addPhone(Long phoneId, Long quantity, Cart cart) {
        checkValues(quantity, cart);
        Stock stock = phoneDao.getStock(phoneId).orElseThrow(() -> new IllegalPhoneException(phoneId));
        if (stock.getPhone().getPrice() == null) {
            throw new IllegalPhoneException(stock.getPhone().getModel());
        }
        if (stock.getStock() - stock.getReserved() - quantity - cart.getCart().getOrDefault(phoneId, 0L) < 1) {
            throw new OutOfStockException(stock.getPhone().getModel(), quantity + cart.getCart().getOrDefault(phoneId, 0L));
        } else {
            cart.getCart().put(phoneId, quantity + cart.getCart().getOrDefault(phoneId, 0L));
            cart.setQuantity(cart.getQuantity() + quantity);
            cart.setTotalPrice(cart.getTotalPrice().add(stock.getPhone().getPrice().multiply(BigDecimal.valueOf(quantity))));
        }
    }

    private void checkValues(Long quantity, Cart cart) {
        if (quantity == null) {
            throw new IllegalArgumentException(QUANTITY_IS_NULL_EXCEPTION_MSG);
        }
        if (cart == null) {
            throw new IllegalArgumentException(CART_IS_NULL_EXCEPTION_MSG);
        }
        if (quantity < 1) {
            throw new IllegalArgumentException(QUANTITY_IS_NEGATIVE_EXCEPTION_MESSAGE);
        }
    }

    //TODO
    @Override
    public void update(Map<Long, Long> items, Cart cart) {
        items.forEach((key, value) -> cart.getCart().put(key, value));
    }

    //TODO
    @Override
    public void remove(Long phoneId, Cart cart) {
        cart.getCart().remove(phoneId);
    }

    public void setPhoneDao(PhoneDao phoneDao) {
        this.phoneDao = phoneDao;
    }
}
