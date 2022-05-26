package com.microservices.event.productws.query;

import com.microservices.event.productws.core.data.ProductEntity;
import com.microservices.event.productws.core.data.ProductRepository;
import com.microservices.event.productws.core.events.ProductCreatedEvent;
import lombok.AllArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@ProcessingGroup("product-group")
public class ProductEventsHandler {

    private final ProductRepository productRepository;

    @EventHandler
    public void on(ProductCreatedEvent productCreatedEvent) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(productCreatedEvent, productEntity);
        productRepository.save(productEntity);
    }
}
