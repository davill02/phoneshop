package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.core.model.phone.PhoneService;
import com.es.phoneshop.web.controller.pages.entites.AddingPhoneRow;
import com.es.phoneshop.web.controller.pages.entites.QuickCartForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class QuickCartPageController {
    private static final String SUCCESS_MESSAGE_PART_PATTERN = "We add %s in quantity %s.";
    private static final String MODEL_FIELD = "rows[%d].model";
    private static final String QUANTITY_FIELD = "rows[%d].quantity";
    private static final String ROWS_I_FIELD = "rows[%d]";

    @Resource
    private CartService cartService;
    @Resource
    private PhoneService phoneService;


    @RequestMapping(method = RequestMethod.GET)
    public String getQuickCartPage(Model model) {
        createQuickCartForm(model);
        return "quickCart";
    }

    private void createQuickCartForm(Model model) {
        if (!model.containsAttribute(QUICK_CART_FORM_ATTR)) {
            model.addAttribute(QUICK_CART_FORM_ATTR, new QuickCartForm());
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String updateCart(@Valid QuickCartForm cartForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {
        String message = addToCartValidItemsAndReturnSuccessMessage(cartForm, bindingResult, session);
        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_ATTR, message);
        redirectAttributes.addFlashAttribute(BINDING_RESULT_QUICK_CART_FORM_ATTR, bindingResult);
        return "redirect:/quickCart";
    }

    private String addToCartValidItemsAndReturnSuccessMessage(QuickCartForm quickCartAddingForm, BindingResult bindingResult, HttpSession session) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < quickCartAddingForm.getRows().size(); i++) {
            if (isValidQuickCartFormRow(quickCartAddingForm.getRows().get(i), bindingResult, i)) {
                appendSuccessMessage(quickCartAddingForm.getRows().get(i), message);
                addValidPhoneAndClearPartForm(quickCartAddingForm, session, i);
            }
        }
        return message.toString();
    }

    private void appendSuccessMessage(AddingPhoneRow addingPhoneRow, StringBuilder message) {
        message.append(String.format(SUCCESS_MESSAGE_PART_PATTERN, addingPhoneRow.getModel(), addingPhoneRow.getQuantity()));
    }

    private boolean isValidQuickCartFormRow(AddingPhoneRow addingPhoneRow, BindingResult bindingResult, int i) {
        return !bindingResult.hasFieldErrors(String.format(MODEL_FIELD, i)) &&
                !bindingResult.hasFieldErrors(String.format(QUANTITY_FIELD, i))
                && !bindingResult.hasFieldErrors(String.format(ROWS_I_FIELD, i))
                && StringUtils.hasText(addingPhoneRow.getModel())
                && addingPhoneRow.getQuantity() != null;
    }

    private void addValidPhoneAndClearPartForm(QuickCartForm quickCartForm, HttpSession session, int i) {
        cartService.addPhone(phoneService.getIdByModel(quickCartForm.getRows().get(i).getModel()).get(),
                (quickCartForm.getRows().get(i).getQuantity()), (Cart) session.getAttribute(CART_ATTR));
        clearValidElement(quickCartForm, i);
    }

    private void clearValidElement(QuickCartForm quickCartAddingForm, int i) {
        quickCartAddingForm.getRows().get(i).setModel("");
        quickCartAddingForm.getRows().get(i).setQuantity(null);
    }

}
