package com.es.core.model.order;

import com.es.core.model.order.mappers.Mapper;
import com.es.core.model.order.mappers.OrderItemMapper;
import com.es.core.model.order.mappers.OrderItemRowMapper;
import com.es.core.model.order.mappers.OrderMapper;
import com.es.core.model.order.mappers.OrderRowMapper;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JdbcOrderDao implements OrderDao {
    private static final String SELECT_COUNT_ORDERS_WITH_UUID = "SELECT COUNT(*) FROM orders WHERE uuid = ?";
    private static final String SELECT_PHONE_2_ORDER_WITH_ORDER_ID = "SELECT * FROM order2phone WHERE orderId = ?";
    private static final String SELECT_ORDERS_WITH_ID = "SELECT * FROM orders WHERE id = ?";
    private static final String SELECT_ORDERS_WITH_UUID = "SELECT * FROM orders WHERE uuid = ?";
    private static final String SELECT_ALL_ORDERS = "SELECT * FROM orders";
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private PhoneDao phoneDao;

    private SimpleJdbcInsert simpleJdbcInsertOrder;
    private SimpleJdbcInsert simpleJdbcInsertOrder2Phone;
    private OrderRowMapper orderRowMapper;
    private OrderItemRowMapper orderItemRowMapper;
    private Mapper<OrderItem> orderItemMapper;
    private Mapper<Order> orderMapper;

    @PostConstruct
    public void init() {
        simpleJdbcInsertOrder = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(OrderDaoConstants.ORDERS_TABLE_NAME)
                .usingGeneratedKeyColumns(OrderDaoConstants.ID_COLUMN);
        simpleJdbcInsertOrder2Phone = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(OrderDaoConstants.ORDER_2_PHONE_TABLE_NAME)
                .usingGeneratedKeyColumns(OrderDaoConstants.ID_COLUMN);
        orderRowMapper = new OrderRowMapper();
        orderItemRowMapper = new OrderItemRowMapper();
        orderMapper = new OrderMapper();
        orderItemMapper = new OrderItemMapper();
    }

    public Optional<Order> getOrder(String uuid) {
        if (uuid == null) {
            return Optional.empty();
        }
        List<Order> orders = jdbcTemplate.query(SELECT_ORDERS_WITH_UUID, orderRowMapper, uuid);
        return getOrderOptional(orders);
    }

    private Optional<Order> getOrderOptional(List<Order> orders) {
        Optional<Order> result = Optional.empty();
        if (orders.size() != 0) {
            Order order = orders.get(0);
            setOrderItemList(order);
            result = Optional.of(order);
        }
        return result;
    }

    @Override
    public Optional<Order> getOrder(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        List<Order> orders = jdbcTemplate.query(SELECT_ORDERS_WITH_ID, orderRowMapper, id);
        return getOrderOptional(orders);
    }

    private void setOrderItemList(Order order) {
        List<OrderItem> orderItemsList = jdbcTemplate.query(SELECT_PHONE_2_ORDER_WITH_ORDER_ID, orderItemRowMapper, order.getId());
        orderItemsList.forEach(orderItem -> {
            Phone phone = phoneDao.get(orderItem.getPhoneId()).orElse(null);
            orderItem.setPhone(phone);
            orderItem.setOrder(order);
        });
        order.setOrderItems(orderItemsList);
    }

    @Override
    public void saveOrder(Order order) {
        validateOrder(order);
        setUuid(order);
        order.setDate(new Timestamp((new Date()).getTime()));
        order.setId((Long) simpleJdbcInsertOrder.executeAndReturnKey(orderMapper.map(order)));
        order.getOrderItems()
                .forEach(orderItem -> orderItem.setId((Long) simpleJdbcInsertOrder2Phone.executeAndReturnKey(orderItemMapper.map(orderItem))));
    }

    private void setUuid(Order order) {
        while (true) {
            String uuid = UUID.randomUUID().toString();
            Long count = jdbcTemplate.queryForObject(SELECT_COUNT_ORDERS_WITH_UUID, Long.class, uuid);
            if (count.equals(0L)) {
                order.setUuid(uuid);
                break;
            }
        }
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order is null");
        }
    }

    @Override
    public List<Order> getAll() {
        List<Order> orders = jdbcTemplate.query(SELECT_ALL_ORDERS, orderRowMapper);
        orders.forEach(this::setOrderItemList);
        return orders;
    }

    @Override
    public void changeOrderStatus(Long id, OrderStatus newStatus) {
        jdbcTemplate.update("UPDATE orders SET status = ? where id = ?", newStatus.toString(), id);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setPhoneDao(PhoneDao phoneDao) {
        this.phoneDao = phoneDao;
    }
}
