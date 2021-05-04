package com.es.phoneshop.web.controller.ajax.convertors;

import com.es.core.cart.Cart;
import com.es.phoneshop.web.controller.ajax.AjaxMessageCode;
import com.es.phoneshop.web.controller.ajax.entities.CartDto;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class CartToCartDtoTest {
    @Test
    public void shouldConvertCart() {
        Cart cart = new Cart();
        cart.setQuantity(100L);
        cart.setTotalPrice(BigDecimal.TEN);
        CartToCartDto cartToCartDTO = new CartToCartDto();

        CartDto cartDTO = cartToCartDTO.convert(cart);

        assertEquals(cart.getQuantity(), cartDTO.getQuantity());
        assertEquals(cart.getTotalPrice(), cartDTO.getTotalPrice());
        assertEquals(cartToCartDTO.ADDED_TO_CART_MSG, cartDTO.getMessage());
        assertEquals(AjaxMessageCode.SUCCESS.code, cartDTO.getCode());
    }
}