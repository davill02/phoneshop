package com.es.core.model.order.mappers;

import com.es.core.model.order.OrderDaoConstants;
import com.es.core.model.order.OrderItem;

import java.util.HashMap;
import java.util.Map;

public class OrderItemMapper implements Mapper<OrderItem> {
    @Override
    public Map<String, Object> map(OrderItem source) {
        Map<String, Object> propName2Value = new HashMap<>();
        propName2Value.put(OrderDaoConstants.PHONE_ID_COLUMN, source.getPhoneId());
        propName2Value.put(OrderDaoConstants.ORDER_ID_COLUMN, source.getOrder().getId());
        propName2Value.put(OrderDaoConstants.QUANTITY_COLUMN, source.getQuantity());
        return propName2Value;
    }
}
