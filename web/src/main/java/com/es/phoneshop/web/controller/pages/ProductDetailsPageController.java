package com.es.phoneshop.web.controller.pages;

import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.Optional;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.PHONE_DETAILS_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.PHONE_DETAILS_PAGE;

@Controller
@RequestMapping(value = "/productDetails")
public class ProductDetailsPageController {
    @Resource
    private PhoneService phoneService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String showProductDetails(Model model, @PathVariable("id") String id) {
        Optional<Phone> phone = phoneService.get(Long.valueOf(id));
        if (phone.isPresent()) {
            model.addAttribute(PHONE_DETAILS_ATTR, phone.get());
            return PHONE_DETAILS_PAGE;
        }
        return "phoneNotFound";
    }


    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String returnProductNotFound() {
        return "phoneNotFound";
    }

}
