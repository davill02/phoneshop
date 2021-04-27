package com.es.phoneshop.web.controller.ajax;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.core.cart.exception.IllegalPhoneException;
import com.es.core.cart.exception.OutOfStockException;
import org.json.JSONObject;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;

@Controller
@RequestMapping(value = "/ajaxCart")
public class AjaxCartController {
    public static final String ADDED_TO_CART = "Added to cart";
    @Resource
    private CartService cartService;

    public AjaxCartController() {
    }

    public AjaxCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addPhone(@Valid PhoneAddingForm phoneAddingForm, HttpSession session) {
        Cart currentCart = cartService.getCart((Cart) session.getAttribute(CART_ATTR));
        session.setAttribute(CART_ATTR, currentCart);
        cartService.addPhone(phoneAddingForm.getPhoneId(), Long.parseLong(phoneAddingForm.getQuantity()), currentCart);
        JSONObject jsonObject = new JSONObject(new AjaxMessage(AjaxMessageCode.SUCCESS.code, ADDED_TO_CART, currentCart.getTotalPrice(), currentCart.getQuantity()));
        return ResponseEntity.ok().body(jsonObject.toString());
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler({BindException.class})
    public ResponseEntity<String> handleBindException(BindException exception) {
        StringBuilder builder = new StringBuilder();
        if (exception.getBindingResult() != null) {
            exception.getAllErrors().forEach(ex -> builder.append(ex.getDefaultMessage()).append("\n"));
        }
        JSONObject jsonObject = new JSONObject(new AjaxMessage(AjaxMessageCode.ERROR.code, builder.toString()));
        return ResponseEntity.ok().body(jsonObject.toString());
    }

    @ExceptionHandler({OutOfStockException.class, IllegalPhoneException.class})
    public ResponseEntity<String> handleCartException(RuntimeException exception) {
        JSONObject jsonObject = new JSONObject(new AjaxMessage(AjaxMessageCode.ERROR.code, exception.getMessage()));
        return ResponseEntity.ok().body(jsonObject.toString());
    }

}
