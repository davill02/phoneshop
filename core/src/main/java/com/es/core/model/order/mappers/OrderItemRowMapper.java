package com.es.core.model.order.mappers;

import com.es.core.model.order.OrderDaoConstants;
import com.es.core.model.order.OrderItem;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemRowMapper implements RowMapper<OrderItem> {
    @Override
    public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(rs.getLong(OrderDaoConstants.ID_COLUMN));
        orderItem.setPhoneId(rs.getLong(OrderDaoConstants.PHONE_ID_COLUMN));
        orderItem.setQuantity(rs.getLong(OrderDaoConstants.QUANTITY_COLUMN));
        return orderItem;
    }
}
