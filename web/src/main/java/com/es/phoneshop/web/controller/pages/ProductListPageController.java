package com.es.phoneshop.web.controller.pages;

import com.es.core.model.phone.PhoneDao;
import com.es.phoneshop.web.controller.ControllerUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.COUNT_PHONES_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.PHONES_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.PRODUCT_LIST_PAGE;

@Controller
@RequestMapping(value = "/productList")
public class ProductListPageController {
    public static final int COUNT_PRODUCTS_ON_PAGE = 10;
    @Resource
    private PhoneDao phoneDao;

    @RequestMapping(method = RequestMethod.GET)
    public String showProductList(Model model, @RequestParam(required = false) String query, @RequestParam(required = false) String order,
                                  @RequestParam(required = false) String sort, @RequestParam(required = false) Integer page, HttpSession session) {
        ControllerUtils.createCartIfNotExist(session);
        model.addAttribute(COUNT_PHONES_ATTR, phoneDao.countResultsFindAllOrderBy(query));
        int p = (page != null) ? page : 1;
        model.addAttribute(PHONES_ATTR, phoneDao.findAllOrderBy(COUNT_PRODUCTS_ON_PAGE * (p - 1), COUNT_PRODUCTS_ON_PAGE, order, sort, query));
        return PRODUCT_LIST_PAGE;
    }


}
