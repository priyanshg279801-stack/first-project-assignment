package com.example.demoEcommerce.services;

import com.example.demoEcommerce.dto.OrderItemRequest;
import com.example.demoEcommerce.dto.OrderRequest;
import com.example.demoEcommerce.entities.Customer;
import com.example.demoEcommerce.entities.OrderItem;
import com.example.demoEcommerce.entities.Product;
import com.example.demoEcommerce.entity.Order;
import com.example.demoEcommerce.exceptions.InsufficientStockException;
import com.example.demoEcommerce.exceptions.ResourceNotFoundException;
import com.example.demoEcommerce.repositories.CustomerRepository;
import com.example.demoEcommerce.repositories.OrderRepository;
import com.example.demoEcommerce.repositories.ProductRepository;
import com.example.demoEcommerce.status.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    /**
     * Create a new order (atomic transaction)
     */
    public Order createOrder(OrderRequest request) {

        log.info("Creating order for customerId: {}", request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + request.getCustomerId()));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Step 1: Validate stock first (prevents partial updates)
        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getProductName());
            }
        }

        // Step 2: Process order items safely
        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemRequest.getProductId()));

            int updatedStock = product.getStockQuantity() - itemRequest.getQuantity();
            product.setStockQuantity(updatedStock);
            productRepository.save(product);

            BigDecimal subTotal =
                    product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubTotal(subTotal);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subTotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully with id: {}", savedOrder.getOrderId());

        return savedOrder;
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {

        Order order = getOrderById(orderId);
        order.setStatus(status);

        Order updated = orderRepository.save(order);

        log.info("Order {} status updated to {}", orderId, status);

        return updated;
    }

    public void deleteOrder(Long id) {

        Order order = getOrderById(id);
        orderRepository.delete(order);

        log.info("Order deleted with id: {}", id);
    }
}