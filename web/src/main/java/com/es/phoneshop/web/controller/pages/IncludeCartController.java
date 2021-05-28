package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.Cart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;

@Controller
@RequestMapping("include/cart")
public class IncludeCartController {
    @GetMapping
    public String includeCart(HttpSession session) {
        if (session.getAttribute(CART_ATTR) == null) {
            session.setAttribute(CART_ATTR, new Cart());
        }
        return "cartHeader";
    }
}
