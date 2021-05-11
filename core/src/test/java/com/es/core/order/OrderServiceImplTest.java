package com.es.core.order;

import com.es.core.cart.Cart;
import com.es.core.cart.CartItem;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDao;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@TestPropertySource("/application.properties")
@ContextConfiguration("/test-order-service.xml")
public class OrderServiceImplTest {
    private static final long QUANTITY = 10L;
    @Resource
    private OrderService orderService;
    @Resource
    private PhoneDao phoneDao;
    @Resource
    private OrderDao orderDao;

    @Test
    public void shouldCreateOrder() {
        Cart cart = getCart();

        Order order = orderService.createOrder(cart);

        assertNotNull(order);
        assertEquals(BigDecimal.valueOf(10.0), order.getDeliveryPrice());
        assertEquals(OrderStatus.NEW, order.getStatus());
    }

    private Cart getCart() {
        Cart cart = new Cart();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setItems(new ArrayList<>());
        CartItem cartItem = new CartItem();
        cartItem.setPhone(new Phone());
        cart.getItems().add(cartItem);
        return cart;
    }


    @Test
    public void shouldPlaceOrder() throws OutOfStockException {
        Order order = getOrderWith3items();

        orderService.placeOrder(order, new PersonalDataForm());

        verify(phoneDao, times(3)).decreaseStock(any(), any());
        verify(orderDao).saveOrder(any(Order.class));
    }

    @Test
    public void shouldGetOrderByUuid() {
        orderService.getOrderByUuid(UUID.randomUUID().toString());

        verify(orderDao).getOrder(anyString());
    }

    @Test(expected = OutOfStockException.class)
    public void shouldThrowOutOfStockException() throws OutOfStockException {
        Order order = getOrderWith3items();
        doThrow(IllegalArgumentException.class).when(phoneDao).decreaseStock(any(), eq(QUANTITY));
        try {
            orderService.placeOrder(order, new PersonalDataForm());
        } finally {
            assertEquals(2, order.getOrderItems().size());
        }
    }

    private Order getOrderWith3items() {
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        order.setUuid(UUID.randomUUID().toString());
        order.setDeliveryPrice(BigDecimal.TEN);
        order.setSubtotal(BigDecimal.TEN);
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            orderItems.add(new OrderItem());
        }
        orderItems.get(1).setQuantity(QUANTITY);
        order.setOrderItems(orderItems);
        return order;
    }

}