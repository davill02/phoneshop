package com.es.core.order;

import com.es.core.cart.Cart;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDao;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import com.es.core.model.phone.PhoneDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private PhoneDao phoneDao;
    @Resource
    private OrderDao orderDao;
    @Value("${delivery.price}")
    private String deliveryPrice;

    @Override
    public Order createOrder(Cart validCart) {
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);
        setPrices(validCart, order);
        setOrderItems(validCart, order);
        return order;
    }

    private void setOrderItems(Cart validCart, Order order) {
        List<OrderItem> orderItemList = new ArrayList<>();
        validCart.getItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPhone(cartItem.getPhone());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPhoneId(cartItem.getPhone().getId());
            orderItemList.add(orderItem);
        });
        order.setOrderItems(orderItemList);
    }

    private void setPrices(Cart validCart, Order order) {
        order.setSubtotal(validCart.getTotalPrice());
        order.setDeliveryPrice(BigDecimal.valueOf(Double.parseDouble(deliveryPrice)));
        order.setTotalPrice(order.getDeliveryPrice().add(order.getSubtotal()));
    }

    private void setPersonalData(PersonalDataForm validForm, Order order) {
        order.setLastName(validForm.getLastname());
        order.setFirstName(validForm.getFirstname());
        order.setContactPhoneNo(validForm.getPhoneNumber());
        order.setAdditionalInformation(validForm.getAdditionalInformation());
        order.setDeliveryAddress(validForm.getDeliveryAddress());
    }

    @Override
    public void placeOrder(Order order, PersonalDataForm personalDataForm) throws OutOfStockException {
        setPersonalData(personalDataForm, order);
        List<OrderItem> orderItems = order.getOrderItems();
        ListIterator<OrderItem> iterator = orderItems.listIterator();
        boolean isOutOfStock = decreaseStockAndIsOutOfStock(iterator);
        if (!order.getOrderItems().isEmpty()) {
            orderDao.saveOrder(order);
        }
        if (isOutOfStock) {
            throw new OutOfStockException();
        }
    }

    @Override
    public Optional<Order> getOrderByUuid(String uuid) {
        return orderDao.getOrder(uuid);
    }

    private boolean decreaseStockAndIsOutOfStock(ListIterator<OrderItem> iterator) {
        boolean outOfStock = false;
        while (iterator.hasNext()) {
            OrderItem item = iterator.next();
            try {
                phoneDao.decreaseStock(item.getPhoneId(), item.getQuantity());
            } catch (IllegalArgumentException e) {
                iterator.remove();
                outOfStock = true;
            }
        }
        return outOfStock;
    }

    public void setPhoneDao(PhoneDao phoneDao) {
        this.phoneDao = phoneDao;
    }

    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
    }
}
