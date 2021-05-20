package com.es.core.order;

import com.es.core.cart.Cart;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Cart cart);

    void placeOrder(Order order, PersonalDataForm personalDataForm) throws OutOfStockException;

    Optional<Order> getOrderByUuid(String uuid);

    List<Order> getAll();

    Optional<Order> getOrderById(Long id);

    void changeOrderStatus(Long id, String newOrderStatus);
}
