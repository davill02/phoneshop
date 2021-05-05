package com.es.phoneshop.web.controller.ajax.convertors;

import com.es.core.cart.Cart;
import com.es.phoneshop.web.controller.ajax.AjaxMessageCode;
import com.es.phoneshop.web.controller.ajax.entities.CartDTO;
import org.springframework.core.convert.converter.Converter;

final public class CartToCartDTO implements Converter<Cart, CartDTO> {

    public static final String ADDED_TO_CART_MSG = "Added to cart";

    @Override
    public CartDTO convert(Cart source) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCode(AjaxMessageCode.SUCCESS.code);
        cartDTO.setMessage(ADDED_TO_CART_MSG);
        cartDTO.setQuantity(source.getQuantity());
        cartDTO.setTotalPrice(source.getTotalPrice());
        return cartDTO;
    }
}
