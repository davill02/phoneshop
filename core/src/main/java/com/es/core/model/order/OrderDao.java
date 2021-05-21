package com.es.core.model.order;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Optional<Order> getOrder(Long id);

    Optional<Order> getOrder(String uuid);

    void saveOrder(Order order);

    List<Order> getAllWithoutOrderItemList();

    void changeOrderStatus(Long id, OrderStatus newStatus);
}
