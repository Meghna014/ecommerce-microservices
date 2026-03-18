package com.ecommerce.notificationservice.consumer;

import com.ecommerce.notificationservice.event.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @KafkaListener(topics = "order-placed", groupId = "notification-group")
    public void handleOrderPlacedEvent(OrderPlacedEvent event)
    {
        System.out.println("==========================================");
        System.out.println("Notification Received!");
        System.out.println("Order ID: " + event.getOrderId());
        System.out.println("Customer ID: " + event.getCustomerId());
        System.out.println("Products ordered: " + event.getProductIds());
        System.out.println("==========================================");

    }

}
