package com.es.phoneshop.web.controller.ajax.convertors;

import com.es.core.cart.Cart;
import com.es.phoneshop.web.controller.ajax.AjaxMessageCode;
import com.es.phoneshop.web.controller.ajax.entities.CartDto;
import org.springframework.core.convert.converter.Converter;

final public class CartToCartDto implements Converter<Cart, CartDto> {

    public static final String ADDED_TO_CART_MSG = "Added to cart";

    @Override
    public CartDto convert(Cart source) {
        CartDto cartDTO = new CartDto();
        cartDTO.setCode(AjaxMessageCode.SUCCESS.code);
        cartDTO.setMessage(ADDED_TO_CART_MSG);
        cartDTO.setQuantity(source.getQuantity());
        cartDTO.setTotalPrice(source.getTotalPrice());
        return cartDTO;
    }
}
