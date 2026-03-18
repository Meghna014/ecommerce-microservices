package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.event.OrderPlacedEvent;
import com.ecommerce.orderservice.exception.OrderNotFoundException;
import com.ecommerce.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public Order placeOrder(Long customerId)
    {

        Map<String, Object> cart =  restTemplate.getForObject("http://cart-service/cart/"+customerId,Map.class);
        Order order = new Order();

        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) cart.get("items");
        if(cartItems == null || cartItems.isEmpty())
        {
            throw new IllegalArgumentException("Cart is empty!");
        }
        else
        {
            order.setOrderDate( LocalDateTime.now());
            order.setStatus("CREATED");
            order.setCustomerId(Long.valueOf(cart.get("customerId").toString()));
            List<OrderItem> orderItems = cartItems.stream()
                    .map(entry -> {
                        OrderItem item = new OrderItem();
                        item.setProductId(Long.valueOf(entry.get("productId").toString()));
                        item.setQuantity(Integer.valueOf(entry.get("quantity").toString()));
                        item.setOrder(order);
                        return item;
                    }).collect(Collectors.toList());
            order.setItems(orderItems);
            orderRepository.save(order);

        }

        //delete the cart
         restTemplate.delete("http://cart-service/"+customerId+"/clear");

        //send order place event
        List<Long> productIds = order.getItems().stream()
                .map(OrderItem :: getProductId)
                .collect(Collectors.toList());

        OrderPlacedEvent event = new OrderPlacedEvent(order.getId(),order.getCustomerId(),productIds);

        kafkaTemplate.send("order-placed",event);

        return  order;

    }

    public  Order getOrderById(Long orderId)
    {
       return orderRepository.findById(orderId).orElseThrow(
               () ->  new OrderNotFoundException("Order  not found with ID "+orderId));
    }

    public List<Order> getOrderByCustomerId(Long customerId)
    {
        return orderRepository.findByCustomerId(customerId);
    }



}