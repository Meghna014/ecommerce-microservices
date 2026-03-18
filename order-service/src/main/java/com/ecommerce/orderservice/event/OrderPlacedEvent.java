package com.ecommerce.orderservice.event;

import java.util.List;

public class OrderPlacedEvent {
    private Long orderId;
    private Long customerId;
    private List<Long> productIds;

    public OrderPlacedEvent() {}

    public OrderPlacedEvent(Long orderId, Long customerId, List<Long> productIds) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productIds = productIds;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
}