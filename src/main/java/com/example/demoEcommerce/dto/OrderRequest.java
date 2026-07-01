package com.example.demoEcommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotNull
    private Long customerId;

    @Valid
    @NotEmpty
    private List<OrderItemRequest> items;
}
