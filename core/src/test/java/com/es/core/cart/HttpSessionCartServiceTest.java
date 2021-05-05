package com.es.core.cart;

import com.es.core.cart.exception.IllegalPhoneException;
import com.es.core.cart.exception.OutOfStockException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration("/test-jdbc-phone-dao-conf.xml")
public class HttpSessionCartServiceTest {

    private static final long LEGAL_PHONE_WITH_STOCK = 1003L;
    private static final long OUT_OF_STOCK_QUANTITY = 1000L;
    private static final long ILLEGAL_PHONE = 1000L;
    public static final long PHONE_WITHOUT_PRICE = 1001L;
    private static final long QUANTITY = 10L;
    private static final long NEGATIVE_QUANTITY = -1L;
    private static final long ONE_ITEM = 1L;
    @Resource
    private CartService cartService;


    @Test(expected = OutOfStockException.class)
    public void shouldAddPhoneAndThrowOutOfStockException() {
        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, OUT_OF_STOCK_QUANTITY, new Cart());
    }

    @Test(expected = IllegalPhoneException.class)
    public void shouldAddPhoneAndThrowIllegalPhoneException() {
        cartService.addPhone(ILLEGAL_PHONE, OUT_OF_STOCK_QUANTITY, new Cart());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldAddPhoneAndThrowIllegalArgumentExceptionByQuantity() {
        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, NEGATIVE_QUANTITY, new Cart());
    }

    @Test(expected = IllegalPhoneException.class)
    public void shouldAddPhoneAndThrowIllegalPhoneExceptionByNullPhoneId() {
        cartService.addPhone(null, QUANTITY, new Cart());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldAddPhoneAndThrowIllegalPhoneExceptionByNullCart() {
        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, QUANTITY, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldAddPhoneAndThrowIllegalPhoneExceptionByNullQuantity() {
        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, null, new Cart());
    }

    @Test(expected = IllegalPhoneException.class)
    public void shouldAddPhoneAndThrowIllegalPhoneExceptionByNullPrice() {
        cartService.addPhone(PHONE_WITHOUT_PRICE, QUANTITY, new Cart());
    }

    @Test
    public void shouldAddPhoneToCart() {
        Cart cart = new Cart();

        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ONE_ITEM, cart);

        assertTrue(cart.getProductId2Quantity().containsKey(LEGAL_PHONE_WITH_STOCK));
        assertEquals(ONE_ITEM, (long) cart.getProductId2Quantity().get(LEGAL_PHONE_WITH_STOCK));
    }

    @Test
    public void shouldAddTwoPhoneToCart() {
        Cart cart = new Cart();

        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ONE_ITEM, cart);
        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ONE_ITEM, cart);

        assertTrue(cart.getProductId2Quantity().containsKey(LEGAL_PHONE_WITH_STOCK));
        assertEquals(ONE_ITEM * 2, (long) cart.getProductId2Quantity().get(LEGAL_PHONE_WITH_STOCK));
    }
}