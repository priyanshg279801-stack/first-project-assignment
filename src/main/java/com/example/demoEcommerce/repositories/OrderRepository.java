package com.example.demoEcommerce.repositories;

import com.example.demoEcommerce.entity.Order;

import com.example.demoEcommerce.status.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerCustomerId(Long customerId);

    List<Order> findByStatus(OrderStatus status);
}