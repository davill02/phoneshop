package com.es.core.model.order;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration("/test-jdbc-phone-dao-conf.xml")
public class JdbcOrderDaoIntTest {
    private static final long EXIST_ORDER_ID = 100L;
    private static final long ID_WITH_2_ITEMS = 101L;
    private static final long QUANTITY = 10L;
    private static final long PHONE_ID = 1003L;
    private static final long ONE = 1L;
    private static final long NOT_EXIST_ORDER = 1L;
    private static final String FIRSTNAME = "Andrei";
    private static final String LASTNAME = "Artuhanau";
    private static final int TWO_ITEMS = 2;
    @Resource
    private OrderDao orderDao;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Test
    public void shouldGetOrder() {
        Optional<Order> order = orderDao.getOrder(EXIST_ORDER_ID);

        assertTrue(order.isPresent());
        assertEquals(FIRSTNAME, order.get().getFirstName());
        assertEquals(LASTNAME, order.get().getLastName());
        assertEquals(ONE, order.get().getOrderItems().size());
    }

    @Test
    public void shouldGetOrderByUuid() {
        Optional<Order> order = orderDao.getOrder("ac0ed9b8-dde7-4523-b55c-70ae94175509");

        assertTrue(order.isPresent());
        assertEquals(FIRSTNAME, order.get().getFirstName());
        assertEquals(LASTNAME, order.get().getLastName());
        assertEquals(ONE, order.get().getOrderItems().size());
    }

    @Test
    public void shouldGetNull() {
        Optional<Order> order = orderDao.getOrder((Long) null);

        assertFalse(order.isPresent());
    }

    @Test
    public void shouldGetNonExistId() {
        Optional<Order> order = orderDao.getOrder(NOT_EXIST_ORDER);

        assertFalse(order.isPresent());
    }

    @Test
    public void shouldGetExistOrderWith2Items() {
        Optional<Order> order = orderDao.getOrder(ID_WITH_2_ITEMS);

        assertTrue(order.isPresent());
        assertEquals(TWO_ITEMS, order.get().getOrderItems().size());
        assertNotNull(order.get().getOrderItems().get(0).getPhoneId());
        assertNotNull(order.get().getOrderItems().get(0).getOrder());
        assertNotNull(order.get().getOrderItems().get(0).getPhone());
        assertNotNull(order.get().getOrderItems().get(0).getQuantity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionByOrderNull() {
        orderDao.saveOrder(null);
    }
    @Test
    public void shouldReturnEmptyOptionalWithNullUuid(){
        Optional<Order> order = orderDao.getOrder((String) null);

        assertFalse(order.isPresent());
    }


    @Test
    public void shouldSaveOrder() {
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setStatus(OrderStatus.NEW);
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(QUANTITY);
        orderItem.setPhoneId(PHONE_ID);
        orderItem.setOrder(order);
        order.getOrderItems().add(orderItem);

        orderDao.saveOrder(order);
        Long result = jdbcTemplate
                .queryForObject("SELECT COUNT(*) FROM order2phone WHERE id = ?", Long.class, orderItem.getId());

        assertEquals((Long) ONE, result);
        assertNotNull(order.getUuid());
        assertNotNull(order.getId());
        assertNotNull(order.getDate());
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "orders", "id = ?", order.getId());
        JdbcTestUtils.deleteFromTableWhere(jdbcTemplate, "order2phone", "orderId = ?", order.getId());
    }

    @Test
    public void shouldGetAllOrders() {
        List<Order> orders = orderDao.getAllWithoutOrderItemList();

        assertEquals(3, orders.size());
        orders.forEach(order -> assertNotNull(order.getOrderItems()));
    }

    @Test
    public void shouldChangeOrderStatus() {
        orderDao.changeOrderStatus(EXIST_ORDER_ID, OrderStatus.DELIVERED);

        String newStatus = jdbcTemplate.queryForObject("SELECT status FROM orders WHERE id = ?", String.class, EXIST_ORDER_ID);
        assertEquals(OrderStatus.DELIVERED.toString(), newStatus);
    }
}