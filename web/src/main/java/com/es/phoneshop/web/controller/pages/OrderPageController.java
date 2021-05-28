package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.core.model.order.Order;
import com.es.core.order.OrderService;
import com.es.core.order.OutOfStockException;
import com.es.core.order.PersonalDataForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.BINDING_RESULT_PERSONAL_DATA_FORM_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.ORDER_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.ORDER_ITEMS_OUT_OF_STOCK_MSG;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.PERSONAL_DATA_FORM_ATTR;

@Controller
@RequestMapping(value = "/order")
@SessionAttributes(PERSONAL_DATA_FORM_ATTR)
public class OrderPageController {
    @Resource
    private OrderService orderService;
    @Resource
    private CartService cartService;

    @RequestMapping(method = RequestMethod.GET)
    public String getOrder(Model model, HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_ATTR);
        session.setAttribute(ORDER_ATTR, orderService.createOrder(cart));
        if (!model.containsAttribute(PERSONAL_DATA_FORM_ATTR)) {
            model.addAttribute(PERSONAL_DATA_FORM_ATTR, new PersonalDataForm());
        }
        return "order";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String placeOrder(@ModelAttribute(PERSONAL_DATA_FORM_ATTR) @Valid PersonalDataForm personalDataForm, BindingResult bindingResult,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(BINDING_RESULT_PERSONAL_DATA_FORM_ATTR, bindingResult);
            return "redirect:order";
        }
        return handleOrderAndGetRedirectPage(personalDataForm, session, redirectAttributes);
    }

    private String handleOrderAndGetRedirectPage(PersonalDataForm personalDataForm, HttpSession session, RedirectAttributes redirectAttributes) {
        Order order = (Order) session.getAttribute(ORDER_ATTR);
        String redirectingPage = "redirect:order";
        try {
            orderService.placeOrder(order, personalDataForm);
            cartService.clear((Cart) session.getAttribute(CART_ATTR));
            redirectingPage = "redirect:/orderOverview/" + order.getUuid();
        } catch (OutOfStockException exception) {
            redirectAttributes.addFlashAttribute(ORDER_ITEMS_OUT_OF_STOCK_MSG, exception.getUserMessage());
            removeFromCartInvalidItems(session, exception);
        }
        return redirectingPage;
    }

    private void removeFromCartInvalidItems(HttpSession session, OutOfStockException exception) {
        List<Long> removedPhoneIds = exception.getPhoneIds();
        Cart cart = (Cart) session.getAttribute(CART_ATTR);
        removedPhoneIds.forEach(id -> cartService.remove(id, cart));
    }
}
