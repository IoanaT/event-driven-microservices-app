package com.microservices.event.productws.query;

import com.microservices.event.productws.core.events.ProductCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class ProductEventsHandler {

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent){

    }
}
