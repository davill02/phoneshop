package com.es.core.order;

import com.es.core.cart.Cart;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDao;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import com.es.core.model.phone.PhoneDao;
import com.es.core.model.phone.Stock;
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
    private final static String USER_EXCEPTION_MESSAGE = "We don't have %s in quantity %d.\n";
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
        validateOrderItemsQuantity(orderItems);
        orderItems.forEach(orderItem -> phoneDao.decreaseStock(orderItem.getPhoneId(), orderItem.getQuantity()));
        orderDao.saveOrder(order);
    }

    private void validateOrderItemsQuantity(List<OrderItem> orderItems) throws OutOfStockException {
        ListIterator<OrderItem> orderItemListIterator = orderItems.listIterator();
        StringBuilder userExceptionMessage = new StringBuilder();
        List<Long> removedIds = new ArrayList<>();
        while (orderItemListIterator.hasNext()) {
            removeAndAppendMessageIfOutOfStockException(orderItemListIterator, userExceptionMessage, removedIds);
        }
        if (userExceptionMessage.length() != 0) {
            throw new OutOfStockException(userExceptionMessage.toString(), removedIds);
        }
    }

    private void removeAndAppendMessageIfOutOfStockException(ListIterator<OrderItem> orderItemListIterator, StringBuilder userExceptionMessage, List<Long> removedIds) {
        OrderItem orderItem = orderItemListIterator.next();
        Optional<Stock> stockOptional = phoneDao.getStock(orderItem.getPhoneId());
        if (!stockOptional.isPresent() || stockOptional.get().getStock() - orderItem.getQuantity() < 0) {
            removedIds.add(orderItem.getPhoneId());
            orderItemListIterator.remove();
            userExceptionMessage.append(String.format(USER_EXCEPTION_MESSAGE, orderItem.getPhone().getModel(), orderItem.getQuantity()));
        }
    }


    @Override
    public Optional<Order> getOrderByUuid(String uuid) {
        return orderDao.getOrder(uuid);
    }

    @Override
    public List<Order> getAll() {
        return orderDao.getAllWithoutOrderItemList();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderDao.getOrder(id);
    }

    @Override
    public void changeOrderStatus(Long id, String newOrderStatus) {
        OrderStatus status = OrderStatus.valueOf(newOrderStatus);
        orderDao.changeOrderStatus(id, status);
        if (status.equals(OrderStatus.REJECTED)) {
            orderDao.getOrder(id).ifPresent(this::returnOrderStockToPhoneDao);
        }
    }

    private void returnOrderStockToPhoneDao(Order order) {
        order.getOrderItems().forEach(orderItem -> phoneDao.increaseStock(orderItem.getPhoneId(), orderItem.getQuantity()));
    }

    public void setPhoneDao(PhoneDao phoneDao) {
        this.phoneDao = phoneDao;
    }

    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }
}
