package com.es.phoneshop.web.controller.pages.admin;

import com.es.core.model.order.Order;
import com.es.core.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Optional;

import static com.es.phoneshop.web.controller.pages.ControllersConstants.ORDERS_ATTR;
import static com.es.phoneshop.web.controller.pages.ControllersConstants.ORDER_ATTR;

@Controller
@RequestMapping(value = "/admin/orders")
public class OrdersPageController {
    @Resource
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.GET)
    public String getAllOrders(Model model) {
        model.addAttribute(ORDERS_ATTR, orderService.getAll());
        return "admin";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getOrderById(Model model, @PathVariable("id") Long id) {
        Optional<Order> order;
        order = orderService.getOrderById(id);
        if (order.isPresent()) {
            model.addAttribute(ORDER_ATTR, order.get());
            return "adminOrderOverview";
        }
        return "adminOverviewOrderNotFound";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String changeOrderStatus(@PathVariable("id") Long id, @RequestParam("orderStatus") String orderStatus) {
        orderService.changeOrderStatus(id, orderStatus);
        return "redirect:/admin/orders/" + id;
    }

}
