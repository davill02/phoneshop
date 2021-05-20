package com.es.core.cart;

import com.es.core.cart.exception.IllegalPhoneException;
import com.es.core.cart.exception.PhoneOutOfStockException;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import com.es.core.model.phone.Stock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpSessionCartServiceTest {

    private static final long ITEM_3 = 3L;
    private static final long LEGAL_PHONE_WITH_STOCK = 1003L;
    private static final BigDecimal LEGAL_PHONE_PRICE = BigDecimal.valueOf(249.0);
    private static final long OUT_OF_STOCK_QUANTITY = 1000L;
    private static final long NOT_EXIST_STOCK_PHONE_ID = 1000L;
    private static final long PHONE_WITHOUT_PRICE = 1001L;
    private static final long QUANTITY = 10L;
    private static final long ONE_ITEM = 1L;
    private static final long EXPECTED_QUANTITY = 20L;
    public static final int STOCK = 20;
    @InjectMocks
    private final CartService cartService = new HttpSessionCartService();
    @Mock
    private PhoneDao phoneDao;

    @Test(expected = PhoneOutOfStockException.class)
    public void shouldAddPhoneAndThrowOutOfStockException() {
        Stock stock = new Stock(getValidPhone(LEGAL_PHONE_WITH_STOCK), 1, 0);
        when(phoneDao.getStock(eq(LEGAL_PHONE_WITH_STOCK))).thenReturn(Optional.of(stock));

        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, OUT_OF_STOCK_QUANTITY, new Cart());
    }

    @Test(expected = IllegalPhoneException.class)
    public void shouldAddPhoneAndThrowIllegalPhoneException() {
        when(phoneDao.getStock(eq(NOT_EXIST_STOCK_PHONE_ID))).thenReturn(Optional.empty());

        cartService.addPhone(NOT_EXIST_STOCK_PHONE_ID, OUT_OF_STOCK_QUANTITY, new Cart());
    }

    @Test(expected = IllegalPhoneException.class)
    public void shouldAddPhoneAndThrowIllegalPhoneExceptionByNullPhoneId() {
        when(phoneDao.getStock(any())).thenReturn(Optional.empty());

        cartService.addPhone(null, QUANTITY, new Cart());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldAddPhoneAndThrowIllegalPhoneExceptionByNullCart() {
        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, QUANTITY, null);
    }

    @Test(expected = IllegalPhoneException.class)
    public void shouldAddPhoneAndThrowIllegalPhoneExceptionByNullPrice() {
        Stock stock = new Stock(getValidPhone(PHONE_WITHOUT_PRICE), STOCK, 0);
        stock.getPhone().setPrice(null);
        when(phoneDao.getStock(eq(PHONE_WITHOUT_PRICE))).thenReturn(Optional.of(stock));

        cartService.addPhone(PHONE_WITHOUT_PRICE, QUANTITY, new Cart());
    }

    private Phone getValidPhone(Long phoneId) {
        Phone phone = new Phone();
        phone.setPrice(BigDecimal.TEN);
        phone.setId(phoneId);
        return phone;
    }

    @Test
    public void shouldAddPhoneToCart() {
        Cart cart = new Cart();
        Stock stock = new Stock(getValidPhone(LEGAL_PHONE_WITH_STOCK), STOCK, 0);
        when(phoneDao.getStock(eq(LEGAL_PHONE_WITH_STOCK))).thenReturn(Optional.of(stock));

        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ONE_ITEM, cart);

        assertEquals((Long) LEGAL_PHONE_WITH_STOCK, cart.getItems().get(0).getPhone().getId());
        assertEquals(ONE_ITEM, (long) cart.getItems().get(0).getQuantity());
    }

    @Test
    public void shouldAddPhoneAllStockToCart() {
        Cart cart = new Cart();
        Stock stock = new Stock(getValidPhone(LEGAL_PHONE_WITH_STOCK), STOCK, 0);
        when(phoneDao.getStock(eq(LEGAL_PHONE_WITH_STOCK))).thenReturn(Optional.of(stock));

        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, (long) STOCK, cart);

        assertEquals((Long) LEGAL_PHONE_WITH_STOCK, cart.getItems().get(0).getPhone().getId());
        assertEquals(STOCK, (long) cart.getItems().get(0).getQuantity());
    }


    @Test
    public void shouldAddTwoPhoneToCart() {
        Cart cart = new Cart();
        Stock stock = new Stock(getValidPhone(LEGAL_PHONE_WITH_STOCK), STOCK, 0);
        stock.getPhone().setPrice(LEGAL_PHONE_PRICE);
        when(phoneDao.getStock(eq(LEGAL_PHONE_WITH_STOCK))).thenReturn(Optional.of(stock));

        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ONE_ITEM, cart);
        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ONE_ITEM, cart);

        assertEquals((Long) LEGAL_PHONE_WITH_STOCK, cart.getItems().get(0).getPhone().getId());
        assertEquals(ONE_ITEM * 2, (long) cart.getItems().get(0).getQuantity());
        assertEquals(LEGAL_PHONE_PRICE.multiply(BigDecimal.valueOf(ONE_ITEM * 2)), cart.getTotalPrice());
    }

    @Test
    public void shouldFixCartAndAddPhone() {
        Cart cart = getInvalidCart();
        Stock stock = new Stock(getValidPhone(LEGAL_PHONE_WITH_STOCK), STOCK, 0);
        when(phoneDao.getStock(eq(LEGAL_PHONE_WITH_STOCK))).thenReturn(Optional.of(stock));

        cartService.addPhone(LEGAL_PHONE_WITH_STOCK, ITEM_3, cart);
        int resultSize = cart.getItems().size();

        assertEquals(1, resultSize);
        assertEquals(ITEM_3, (long) cart.getQuantity());
    }

    private Cart getInvalidCart() {
        Cart cart = new Cart();
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(new CartItem(null, 1L));
        cartItemList.add(new CartItem(getValidPhone(LEGAL_PHONE_WITH_STOCK), null));
        cart.setItems(cartItemList);
        return cart;
    }


    @Test
    public void shouldRemoveItem() {
        Cart cart = new Cart();
        cart.setQuantity(ONE_ITEM * 2);
        cart.setTotalPrice(LEGAL_PHONE_PRICE.add(BigDecimal.TEN));
        cart.getItems().add(new CartItem(getValidPhone(LEGAL_PHONE_WITH_STOCK), ONE_ITEM));
        cart.getItems().add(createNonExistValidCartItem());

        cartService.remove(LEGAL_PHONE_WITH_STOCK, cart);

        assertEquals(BigDecimal.TEN, cart.getTotalPrice());
        assertEquals((Long) ONE_ITEM, cart.getQuantity());
        assertEquals(1, cart.getItems().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldUpdateAndThrowIllegalArgumentExceptionByNull() {
        cartService.update(null, new Cart());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldUpdateAndThrowIllegalArgumentExceptionByNullCart() {
        cartService.update(new HashMap<>(), null);
    }

    @Test
    public void shouldUpdateCart() {
        Map<Long, Long> map = new HashMap<>();
        map.put(1003L, EXPECTED_QUANTITY);
        map.put(1006L, EXPECTED_QUANTITY);
        Cart cart = getLegalCart();

        cartService.update(map, cart);
        Long result1003L = cart.getItems().get(0).getQuantity();
        Long result1006L = cart.getItems().get(1).getQuantity();

        assertEquals((Long) EXPECTED_QUANTITY, result1003L);
        assertEquals((Long) EXPECTED_QUANTITY, result1006L);
    }

    private Cart getLegalCart() {
        Cart cart = new Cart();
        cart.getItems().add(new CartItem(getValidPhone(1003L), ONE_ITEM));
        cart.getItems().add(new CartItem(getValidPhone(1006L), ONE_ITEM));
        return cart;
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

    @Test
    public void shouldClearCart(){
        Cart cart = getLegalCart();

        cartService.clear(cart);

        assertEquals(0,cart.getItems().size());
        assertEquals(BigDecimal.ZERO,cart.getTotalPrice());
        assertEquals((Long) 0L,cart.getQuantity());
    }
}