package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.core.model.phone.PhoneService;
import com.es.phoneshop.web.controller.ControllerUtils;
import com.es.phoneshop.web.controller.pages.entites.QuickCartAddingForm;
import com.es.phoneshop.web.controller.pages.validators.QuickCartFormValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.BINDING_RESULT_QUICK_CART_FORM_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.CART_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.QUICK_CART_FORM_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.SUCCESS_MESSAGE_ATTR;

@Controller
@SessionAttributes(QUICK_CART_FORM_ATTR)
@RequestMapping(value = "/quickCart")
public class QuickCartPageController {
    private static final String SUCCESS_MESSAGE_PART_PATTERN = "We add %s in quantity %s.";
    private static final String MODEL_FIELD = "model[%d]";
    private static final String QUANTITY_FIELD = "quantity[%d]";

    @Resource
    private CartService cartService;
    @Resource
    private PhoneService phoneService;
    @Resource
    private QuickCartFormValidator validator;

    @InitBinder(QUICK_CART_FORM_ATTR)
    public void init(WebDataBinder dataBinder) {
        dataBinder.setValidator(validator);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getQuickCartPage(HttpSession session, Model model) {
        ControllerUtils.createCartIfNotExist(session);
        createQuickCartForm(model);
        return "quickCart";
    }

    private void createQuickCartForm(Model model) {
        if (!model.containsAttribute(QUICK_CART_FORM_ATTR)) {
            model.addAttribute(QUICK_CART_FORM_ATTR, new QuickCartAddingForm());
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String updateCart(@Valid @ModelAttribute(QUICK_CART_FORM_ATTR) QuickCartAddingForm quickCartAddingForm, BindingResult bindingResult,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        String message = addToCartValidItemsAndReturnSuccessMessage(quickCartAddingForm, bindingResult, session);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_ATTR, message);
        redirectAttributes.addFlashAttribute(BINDING_RESULT_QUICK_CART_FORM_ATTR, bindingResult);
        return "redirect:/quickCart";
    }

    private String addToCartValidItemsAndReturnSuccessMessage(QuickCartAddingForm quickCartAddingForm, BindingResult bindingResult, HttpSession session) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < quickCartAddingForm.getModel().size(); i++) {
            if (isValidQuickCartFormRow(quickCartAddingForm, bindingResult, i)) {
                appendSuccessMessage(quickCartAddingForm, message, i);
                addValidPhoneAndClearPartForm(quickCartAddingForm, session, i);
            }
        }
        return message.toString();
    }

    private void appendSuccessMessage(QuickCartAddingForm quickCartAddingForm, StringBuilder message, int i) {
        message.append(String.format(SUCCESS_MESSAGE_PART_PATTERN, quickCartAddingForm.getModel().get(i), quickCartAddingForm.getQuantity().get(i)));
    }

    private boolean isValidQuickCartFormRow(QuickCartAddingForm quickCartAddingForm, BindingResult bindingResult, int i) {
        return !bindingResult.hasFieldErrors(String.format(MODEL_FIELD, i)) &&
                !bindingResult.hasFieldErrors(String.format(QUANTITY_FIELD, i))
                && StringUtils.hasText(quickCartAddingForm.getModel().get(i))
                && StringUtils.hasText(quickCartAddingForm.getQuantity().get(i));
    }

    private void addValidPhoneAndClearPartForm(QuickCartAddingForm quickCartAddingForm, HttpSession session, int i) {
        cartService.addPhone(phoneService.getIdByModel(quickCartAddingForm.getModel().get(i)).get(),
                Long.parseLong(quickCartAddingForm.getQuantity().get(i)), (Cart) session.getAttribute(CART_ATTR));
        clearValidElement(quickCartAddingForm, i);
    }

    private void clearValidElement(QuickCartAddingForm quickCartAddingForm, int i) {
        quickCartAddingForm.getModel().set(i, "");
        quickCartAddingForm.getQuantity().set(i, "");
    }

}
