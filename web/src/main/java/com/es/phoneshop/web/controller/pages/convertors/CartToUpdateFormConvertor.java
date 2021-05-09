package com.es.phoneshop.web.controller.pages.convertors;

import com.es.core.cart.Cart;
import com.es.core.cart.CartItem;
import com.es.core.model.phone.Phone;
import com.es.phoneshop.web.controller.pages.entites.UpdateForm;
import org.springframework.core.convert.converter.Converter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CartToUpdateFormConvertor implements Converter<Cart, UpdateForm> {
    @Override
    public UpdateForm convert(Cart source) {
        List<Long> ids = source.getItems().stream()
                .map(CartItem::getPhone)
                .map(Phone::getId)
                .collect(Collectors.toList());
        List<String> quantities = source.getItems().stream()
                .map(CartItem::getQuantity)
                .map(Objects::toString)
                .collect(Collectors.toList());
        UpdateForm form = new UpdateForm();
        form.setQuantity(quantities);
        form.setPhoneId(ids);
        return form;
    }
}
