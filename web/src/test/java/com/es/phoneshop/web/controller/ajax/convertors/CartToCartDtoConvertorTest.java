package com.es.phoneshop.web.controller.ajax.convertors;

import com.es.core.cart.Cart;
import com.es.phoneshop.web.controller.ajax.AjaxMessageCode;
import com.es.phoneshop.web.controller.ajax.entities.CartDto;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class CartToCartDtoConvertorTest {
    private final CartToCartDtoConvertor cartToCartDtoConvertor = new CartToCartDtoConvertor();
    @Test
    public void shouldConvertCart() {
        Cart cart = new Cart();
        cart.setQuantity(100L);
        cart.setTotalPrice(BigDecimal.TEN);

        CartDto cartDto = cartToCartDtoConvertor.convert(cart);

        assertEquals(cart.getQuantity(), cartDto.getQuantity());
        assertEquals(cart.getTotalPrice(), cartDto.getTotalPrice());
        assertEquals(CartToCartDtoConvertor.ADDED_TO_CART_MSG, cartDto.getMessage());
        assertEquals(AjaxMessageCode.SUCCESS.getCode(), cartDto.getCode());
    }
}