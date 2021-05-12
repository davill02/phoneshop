package com.es.core.cart;

import com.es.core.cart.exception.IllegalPhoneException;
import com.es.core.cart.exception.OutOfStockException;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration("/test-jdbc-phone-dao-conf.xml")
public class HttpSessionCartServiceTest {

    public static final long ITEM_3 = 3L;
    private static final long LEGAL_PHONE_WITH_STOCK = 1003L;
    private static final BigDecimal LEGAL_PHONE_PRICE = BigDecimal.valueOf(249.0);
    private static final long OUT_OF_STOCK_QUANTITY = 1000L;
    private static final long ILLEGAL_PHONE = 1000L;
    public static final long PHONE_WITHOUT_PRICE = 1001L;
    private static final long QUANTITY = 10L;
    private static final long NEGATIVE_QUANTITY = -1L;
    private static final long ONE_ITEM = 1L;
    @Resource
    private CartService cartService;
    @Resource
    private PhoneDao phoneDao;

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

        assertEquals((Long) LEGAL_PHONE_WITH_STOCK, cart.getItems().get(0).getPhone().getId());
        assertEquals(ONE_ITEM, (long) cart.getItems().get(0).getQuantity());
    }

    @Test
    public void shouldAddTwoPhoneToCart() {
        Cart cart = new Cart();

        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ONE_ITEM, cart);
        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ONE_ITEM, cart);

        assertEquals((Long) LEGAL_PHONE_WITH_STOCK, cart.getItems().get(0).getPhone().getId());
        assertEquals(ONE_ITEM * 2, (long) cart.getItems().get(0).getQuantity());
        assertEquals(LEGAL_PHONE_PRICE.multiply(BigDecimal.valueOf(ONE_ITEM * 2)), cart.getTotalPrice());
    }

    @Test
    public void shouldFixCartAndAddPhone() {
        Cart cart = getInvalidCart();

        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ITEM_3, cart);
        int resultSize = cart.getItems().size();

        assertEquals(1, resultSize);
        assertEquals(ITEM_3, (long) cart.getQuantity());
    }

    private Cart getInvalidCart() {
        Cart cart = new Cart();
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(new CartItem(null, 1L));
        cartItemList.add(new CartItem(phoneDao.get(LEGAL_PHONE_WITH_STOCK).get(), null));
        cart.setItems(cartItemList);
        return cart;
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldAddPhoneWithIllegalCartByMap() {
        Cart cart = new Cart();
        cart.setItems(null);

        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ITEM_3, cart);
    }

    @Test
    public void shouldRemoveItem() {
        Cart cart = new Cart();
        cart.setQuantity(ONE_ITEM * 2);
        cart.setTotalPrice(LEGAL_PHONE_PRICE.add(BigDecimal.TEN));
        cart.getItems().add(new CartItem(phoneDao.get(LEGAL_PHONE_WITH_STOCK).get(), ONE_ITEM));
        cart.getItems().add(createNonExistValidCartItem());

        cartService.remove(LEGAL_PHONE_WITH_STOCK, cart);

        assertEquals(BigDecimal.TEN, cart.getTotalPrice());
        assertEquals((Long) ONE_ITEM, cart.getQuantity());
        assertEquals(1, cart.getItems().size());
    }

    public CartItem createNonExistValidCartItem() {
        CartItem cartItem = new CartItem();
        Phone phone = new Phone();
        phone.setId(100L);
        phone.setPrice(BigDecimal.TEN);
        cartItem.setPhone(phone);
        cartItem.setQuantity(ONE_ITEM);
        return cartItem;
    }
}