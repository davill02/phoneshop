package com.es.core.model.order.mappers;

import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDaoConstants;

import java.util.HashMap;
import java.util.Map;

public class OrderMapper implements Mapper<Order> {
    @Override
    public Map<String, Object> map(Order order) {
        HashMap<String, Object> propName2Value = new HashMap<>();
        propName2Value.put(OrderDaoConstants.ID_COLUMN, order.getId());
        propName2Value.put(OrderDaoConstants.LAST_NAME_COLUMN, order.getLastName());
        propName2Value.put(OrderDaoConstants.FIRST_NAME_COLUMN, order.getFirstName());
        propName2Value.put(OrderDaoConstants.ADDITIONAL_INFORMATION_COLUMN, order.getAdditionalInformation());
        propName2Value.put(OrderDaoConstants.TOTAL_PRICE_COLUMN, order.getTotalPrice());
        propName2Value.put(OrderDaoConstants.SUBTOTAL_COLUMN, order.getSubtotal());
        propName2Value.put(OrderDaoConstants.DELIVERY_ADDRESS_COLUMN, order.getDeliveryAddress());
        propName2Value.put(OrderDaoConstants.DELIVERY_PRICE_COLUMN, order.getDeliveryPrice());
        propName2Value.put(OrderDaoConstants.STATUS_COLUMN, order.getStatus().name());
        propName2Value.put(OrderDaoConstants.CONTACT_PHONE_NO_COLUMN, order.getContactPhoneNo());
        propName2Value.put(OrderDaoConstants.UUID_COLUMN, order.getUuid());
        propName2Value.put(OrderDaoConstants.DATE,order.getDate());
        return propName2Value;
    }
}
