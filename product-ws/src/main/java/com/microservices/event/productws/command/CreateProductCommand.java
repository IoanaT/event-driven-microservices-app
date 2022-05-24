package com.microservices.event.productws.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Builder
@Data
public class CreateProductCommand {
    @TargetAggregateIdentifier
    private final String titleId;
    private final String title;
    private final BigDecimal price;
    private final Integer quantity;

}
