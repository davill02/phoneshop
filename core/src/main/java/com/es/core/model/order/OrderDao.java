package com.es.core.model.order;

import java.util.Optional;

public interface OrderDao {

    Optional<Order> getOrder(Long id);

    Optional<Order> getOrder(String uuid);

    void saveOrder(Order order);
}
