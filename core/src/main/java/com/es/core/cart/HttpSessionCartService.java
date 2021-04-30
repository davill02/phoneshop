package com.es.core.cart;

import com.es.core.cart.exception.IllegalPhoneException;
import com.es.core.cart.exception.OutOfStockException;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import com.es.core.model.phone.Stock;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class HttpSessionCartService implements CartService {

    private static final String QUANTITY_IS_NULL_EXCEPTION_MSG = "Quantity is null";
    private static final String CART_IS_NULL_EXCEPTION_MSG = "Cart is null";
    private static final String QUANTITY_IS_NEGATIVE_EXCEPTION_MESSAGE = "Quantity can't be negative";
    @Resource
    private PhoneDao phoneDao;

    @Override
    public void addPhone(Long phoneId, Long quantity, Cart cart) {
        checkValues(quantity, cart);
        Stock stock = phoneDao.getStock(phoneId).orElseThrow(() -> new IllegalPhoneException(phoneId));
        checkStock(phoneId, quantity, cart, stock);
        cart.getProductId2Quantity().put(phoneId, quantity + cart.getProductId2Quantity().getOrDefault(phoneId, 0L));
        cart.setTotalPrice(calculateTotalPrice(cart));
        cart.setQuantity(calculateQuantity(cart));
    }

    private void validateAndFixCart(Cart cart) {
        Set<Long> ids = cart.getProductId2Quantity().keySet();
        for (Long id : ids) {
            validateAndRemoveIfInvalidStock(cart, id);
            validateAndRemoveIfInvalidQuantity(cart, id);
        }
    }

    private void validateAndRemoveIfInvalidQuantity(Cart cart, Long id) {
        Long quantity = cart.getProductId2Quantity().get(id);
        if (quantity == null || quantity < 1) {
            cart.getProductId2Quantity().remove(id);
        }
    }


    private void validateAndRemoveIfInvalidStock(Cart cart, Long id) {
        Optional<Stock> stock = phoneDao.getStock(id);
        if (!stock.isPresent() || stock.get().getPhone().getPrice() == null) {
            cart.getProductId2Quantity().remove(id);
        }
    }


    private BigDecimal calculateTotalPrice(Cart validCart) {
        return validCart.getProductId2Quantity().entrySet()
                .stream()
                .map(entry -> {
                    Optional<Phone> phone = phoneDao.get(entry.getKey());
                    BigDecimal price = BigDecimal.ZERO;
                    if (phone.isPresent()) {
                        price = phone.get().getPrice().multiply(BigDecimal.valueOf(entry.getValue()));
                    }
                    return price;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Long calculateQuantity(Cart validCart) {
        return validCart.getProductId2Quantity().values().stream().reduce(0L, Long::sum);
    }

    private void checkStock(Long phoneId, Long quantity, Cart cart, Stock stock) {
        if (stock.getPhone().getPrice() == null) {
            throw new IllegalPhoneException(stock.getPhone().getModel());
        }
        if (stock.getStock() - stock.getReserved() - quantity - cart.getProductId2Quantity().getOrDefault(phoneId, 0L) < 1) {
            throw new OutOfStockException(stock.getPhone().getModel(), quantity + cart.getProductId2Quantity().getOrDefault(phoneId, 0L));
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
        items.forEach((key, value) -> cart.getProductId2Quantity().put(key, value));
    }

    //TODO
    @Override
    public void remove(Long phoneId, Cart cart) {
        cart.getProductId2Quantity().remove(phoneId);
    }

    public void setPhoneDao(PhoneDao phoneDao) {
        this.phoneDao = phoneDao;
    }
}
