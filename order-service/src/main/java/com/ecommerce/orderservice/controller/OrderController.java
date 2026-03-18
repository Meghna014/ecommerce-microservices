package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.service.OrderService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.orderservice.entity.Order;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id)
    {
       return  ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable Long customerId)
    {
        return ResponseEntity.ok(orderService.getOrderByCustomerId(customerId)) ;
    }

    @PostMapping("/checkout/{customerId}")
    public ResponseEntity<Order> placeOrder(@PathVariable Long customerId)
    {
        Order order = orderService.placeOrder(customerId);
        return new ResponseEntity(order, HttpStatus.CREATED);
    }
}
