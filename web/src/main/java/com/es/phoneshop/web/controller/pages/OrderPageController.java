package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.Cart;
import com.es.core.model.order.Order;
import com.es.core.order.OrderService;
import com.es.core.order.OutOfStockException;
import com.es.core.order.PersonalDataForm;
import com.es.phoneshop.web.controller.ControllerUtils;
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

import static com.es.phoneshop.web.controller.pages.ControllersConstants.BINDING_RESULT_PERSONAL_DATA_FORM_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.BINDING_RESULT_UPDATE_FORM_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.ORDER_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.PERSONAL_DATA_FORM_ATTR;

@Controller
@RequestMapping(value = "/order")
@SessionAttributes(PERSONAL_DATA_FORM_ATTR)
public class OrderPageController {
    @Resource
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET)
    public String getOrder(Model model, HttpSession session) {
        ControllerUtils.createCartIfNotExist(session);
        Cart cart = (Cart) session.getAttribute(CART_ATTR);
        session.setAttribute(ORDER_ATTR, orderService.createOrder(cart));
        if (!model.containsAttribute(PERSONAL_DATA_FORM_ATTR)) {
            model.addAttribute(PERSONAL_DATA_FORM_ATTR, new PersonalDataForm());
        }
        return "order";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String placeOrder(@ModelAttribute(PERSONAL_DATA_FORM_ATTR) @Valid PersonalDataForm personalDataForm, BindingResult bindingResult,
                             HttpSession session, RedirectAttributes redirectAttributes) throws OutOfStockException {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(BINDING_RESULT_PERSONAL_DATA_FORM_ATTR, bindingResult);
            return "redirect:order";
        }
        Order order = (Order) session.getAttribute(ORDER_ATTR);
        orderService.placeOrder(order, personalDataForm);
        return "redirect:/orderOverview/" + order.getUuid();
    }
}
