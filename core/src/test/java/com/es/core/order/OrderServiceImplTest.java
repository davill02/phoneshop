package com.es.core.order;

import com.es.core.cart.Cart;
import com.es.core.cart.CartItem;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDao;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import com.es.core.model.phone.Stock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {
    private static final long QUANTITY = 10L;
    private static final long ID = 12L;
    @InjectMocks
    private final OrderService orderService = new OrderServiceImpl();
    @Mock
    private PhoneDao phoneDao;
    @Mock
    private OrderDao orderDao;

    @Before
    public void setup() {
        ((OrderServiceImpl) orderService).setDeliveryPrice("10.0");
    }

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
        when(phoneDao.getStock(any())).thenReturn(createStock());

        orderService.placeOrder(order, new PersonalDataForm());

        verify(phoneDao, times(3)).decreaseStock(any(), any());
        verify(orderDao).saveOrder(any(Order.class));
    }

    private Optional<Stock> createStock() {
        Stock stock = new Stock();
        stock.setStock((int) QUANTITY);
        return Optional.of(stock);
    }


    @Test
    public void shouldGetOrderByUuid() {
        orderService.getOrderByUuid(UUID.randomUUID().toString());

        verify(orderDao).getOrder(anyString());
    }

    @Test(expected = OutOfStockException.class)
    public void shouldThrowOutOfStockException() throws OutOfStockException {
        Order order = getOrderWith3items();
        when(phoneDao.getStock(any())).thenReturn(createStock());
        order.getOrderItems().get(0).setQuantity(QUANTITY * 2);

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
            OrderItem orderItem = new OrderItem();
            orderItem.setPhone(new Phone());
            orderItem.setQuantity(QUANTITY);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        return order;
    }
    @Test
    public void shouldChangeStatusToDelivered(){
        orderService.changeOrderStatus(ID,OrderStatus.DELIVERED.toString());

        verify(orderDao).changeOrderStatus(eq(ID), eq(OrderStatus.DELIVERED));
        verify(phoneDao,never()).increaseStock(any(),any());
    }
    @Test
    public void shouldChangeStatusToRejected(){
        when(orderDao.getOrder(eq(ID))).thenReturn(Optional.of(getOrderWith3items()));

        orderService.changeOrderStatus(ID,OrderStatus.REJECTED.toString());

        verify(orderDao).changeOrderStatus(eq(ID), eq(OrderStatus.REJECTED));
        verify(orderDao).getOrder(eq(ID));
        verify(phoneDao,times(3)).increaseStock(any(),any());
    }
}