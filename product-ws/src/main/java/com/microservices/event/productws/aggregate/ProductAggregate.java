package com.microservices.event.productws.aggregate;

import com.google.common.base.Strings;
import com.microservices.event.productws.command.CreateProductCommand;
import com.microservices.event.productws.core.events.ProductCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate
@NoArgsConstructor
public class ProductAggregate {

    @AggregateIdentifier
    private String titleId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    @CommandHandler
    /**
     * Used to validate CreateProductCommand
     */
    public ProductAggregate(CreateProductCommand createProductCommand) {
        if (Strings.isNullOrEmpty(createProductCommand.getTitle())) {
            throw new IllegalArgumentException("Title cannot be empty!");
        }
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();

        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

        AggregateLifecycle.apply(productCreatedEvent);
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        this.price = productCreatedEvent.getPrice();
        this.titleId = productCreatedEvent.getTitleId();
        this.title = productCreatedEvent.getTitle();
        this.quantity = productCreatedEvent.getQuantity();
    }
}
