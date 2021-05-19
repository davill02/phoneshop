package com.es.phoneshop.web.controller.pages;

import com.es.core.model.order.Order;
import com.es.core.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Optional;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.ORDER_ATTR;

@Controller
@RequestMapping(value = "/orderOverview")
public class OrderOverviewPageController {
    @Resource
    private OrderService service;

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
    public String showOverview(@PathVariable String uuid, Model model) {
        Optional<Order> order = service.getOrderByUuid(uuid);
        if (order.isPresent()) {
            model.addAttribute(ORDER_ATTR, order.get());
            return "orderOverview";
        }
        return "orderNotFound";
    }
}
