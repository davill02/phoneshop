package com.es.core.model.order.mappers;

import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDaoConstants;
import com.es.core.model.order.OrderStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.es.core.model.order.OrderDaoConstants.ADDITIONAL_INFORMATION_COLUMN;
import static com.es.core.model.order.OrderDaoConstants.ID_COLUMN;
import static com.es.core.model.order.OrderDaoConstants.TOTAL_PRICE_COLUMN;

public class OrderRowMapper implements RowMapper<Order> {


    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setUuid(rs.getString(OrderDaoConstants.UUID_COLUMN));
        order.setId(rs.getLong(ID_COLUMN));
        order.setAdditionalInformation(rs.getString(ADDITIONAL_INFORMATION_COLUMN));
        order.setContactPhoneNo(rs.getString(OrderDaoConstants.CONTACT_PHONE_NO_COLUMN));
        order.setDeliveryAddress(rs.getString(OrderDaoConstants.DELIVERY_ADDRESS_COLUMN));
        order.setFirstName(rs.getString(OrderDaoConstants.FIRST_NAME_COLUMN));
        order.setLastName(rs.getString(OrderDaoConstants.LAST_NAME_COLUMN));
        order.setDeliveryPrice(rs.getBigDecimal(OrderDaoConstants.DELIVERY_PRICE_COLUMN));
        order.setTotalPrice(rs.getBigDecimal(TOTAL_PRICE_COLUMN));
        order.setSubtotal(rs.getBigDecimal(OrderDaoConstants.SUBTOTAL_COLUMN));
        order.setStatus(OrderStatus.valueOf(rs.getString(OrderDaoConstants.STATUS_COLUMN)));
        return order;
    }
}
