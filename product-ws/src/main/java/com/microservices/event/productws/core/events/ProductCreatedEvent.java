package com.microservices.event.productws.core.events;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreatedEvent {
    private String titleId;
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
