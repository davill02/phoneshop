package com.es.phoneshop.web.controller.ajax;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.core.cart.exception.IllegalPhoneException;
import com.es.core.cart.exception.OutOfStockException;
import com.es.phoneshop.web.controller.ajax.entities.CartDTO;
import com.es.phoneshop.web.controller.ajax.entities.ExceptionMessageDTO;
import com.es.phoneshop.web.controller.ajax.entities.PhoneAddingForm;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;

@Controller
@RequestMapping(value = "/ajaxCart")
public class AjaxCartController {
    @Resource
    private CartService cartService;
    @Resource
    private ConversionService conversionService;

    public AjaxCartController() {
    }

    public AjaxCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CartDTO addPhone(@Valid PhoneAddingForm phoneAddingForm, HttpSession session) {
        createCartIfNotExist(session);
        Cart currentCart = (Cart) session.getAttribute(CART_ATTR);
        cartService.addPhone(phoneAddingForm.getPhoneId(), Long.parseLong(phoneAddingForm.getQuantity()), currentCart);
        return conversionService.convert(currentCart, CartDTO.class);
    }

    private void createCartIfNotExist(HttpSession session) {
        if (session.getAttribute(CART_ATTR) == null) {
            session.setAttribute(CART_ATTR, new Cart());
        }
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ExceptionHandler({BindException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ExceptionMessageDTO handleBindException(BindException exception) {
        StringBuilder builder = new StringBuilder();
        if (exception.getBindingResult() != null) {
            exception.getAllErrors().forEach(ex -> builder.append(ex.getDefaultMessage()).append("\n"));
        }
        return new ExceptionMessageDTO(AjaxMessageCode.ERROR.code, builder.toString());
    }

    @ExceptionHandler({OutOfStockException.class, IllegalPhoneException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ExceptionMessageDTO handleCartException(RuntimeException exception) {
        return new ExceptionMessageDTO(AjaxMessageCode.ERROR.code, exception.getMessage());
    }

}
