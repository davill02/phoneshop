package com.es.phoneshop.web.controller.pages;

import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Optional;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.PHONE_DETAILS;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.PHONE_DETAILS_PAGE;

@Controller
@RequestMapping(value = "/productDetails")
public class ProductDetailsPageController {
    @Resource
    private PhoneDao phoneDao;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String showProductDetails(Model model, @PathVariable("id") String id) {
        Optional<Phone> phone = phoneDao.get(Long.valueOf(id));
        if (phone.isPresent()) {
            model.addAttribute(PHONE_DETAILS, phone.get());
            return PHONE_DETAILS_PAGE;
        }
        return "phone-not-found";
    }

    @ExceptionHandler(Exception.class)
    public void exceptionHandler(Exception e) {
        System.out.println(e.getMessage());
        System.out.println(e.getClass());
        e.printStackTrace();
    }
}
