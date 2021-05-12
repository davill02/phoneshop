package com.es.phoneshop.web.controller;

import com.es.core.cart.Cart;

import javax.servlet.http.HttpSession;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;

public final class ControllerUtils {
    private ControllerUtils() {
    }

    public static void createCartIfNotExist(HttpSession session) {
        if (session.getAttribute(CART_ATTR) == null) {
            session.setAttribute(CART_ATTR, new Cart());
        }
    }
}
