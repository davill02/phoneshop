package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.phoneshop.web.controller.ControllerUtils;
import com.es.phoneshop.web.controller.pages.entites.UpdateForm;
import com.es.phoneshop.web.controller.pages.validators.UpdateFormValidator;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.BINDING_RESULT_UPDATE_FORM_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.ORDER_ACTION;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.UPDATE_FORM_ATTR;

@Controller
@SessionAttributes(UPDATE_FORM_ATTR)
@RequestMapping(value = "/cart")
public class CartPageController {
    @Resource
    private CartService cartService;
    @Resource
    private UpdateFormValidator validator;
    @Resource
    private ConversionService conversionService;

    @InitBinder(UPDATE_FORM_ATTR)
    public void init(WebDataBinder dataBinder) {
        dataBinder.setValidator(validator);
    }

    @RequestMapping(method = RequestMethod.GET)
    public void getCart(HttpSession session, Model model) {
        ControllerUtils.createCartIfNotExist(session);
        if (!model.containsAttribute(UPDATE_FORM_ATTR)) {
            model.addAttribute(UPDATE_FORM_ATTR, conversionService.convert(session.getAttribute(CART_ATTR), UpdateForm.class));
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void removeItem(Long id, HttpSession session) {
        cartService.remove(id, (Cart) session.getAttribute(CART_ATTR));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String updateCart(@Valid @ModelAttribute(UPDATE_FORM_ATTR) UpdateForm updateForm, BindingResult bindingResult,
                             HttpSession session, RedirectAttributes redirectAttributes, @RequestParam("action") String action) {
        if (!bindingResult.hasErrors()) {
            cartService.update(conversionService.convert(updateForm, Map.class), (Cart) session.getAttribute(CART_ATTR));
        } else {
            redirectAttributes.addFlashAttribute(BINDING_RESULT_UPDATE_FORM_ATTR, bindingResult);
        }
        if (isOrdered(bindingResult, action)) {
            return "redirect:order";
        }
        return "redirect:cart";
    }

    private boolean isOrdered(BindingResult bindingResult, String action) {
        return action != null && action.equals(ORDER_ACTION) && !bindingResult.hasErrors();
    }
}
