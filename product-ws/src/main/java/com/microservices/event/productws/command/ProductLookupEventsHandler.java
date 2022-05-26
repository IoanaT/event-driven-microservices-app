package com.microservices.event.productws.command;

import com.microservices.event.productws.core.data.ProductLookupEntity;
import com.microservices.event.productws.core.data.ProductLookupRepository;
import com.microservices.event.productws.core.events.ProductCreatedEvent;
import lombok.AllArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
@AllArgsConstructor
public class ProductLookupEventsHandler {

    private final ProductLookupRepository productLookupRepository;

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductLookupEntity productLookupEntity =
                new ProductLookupEntity(event.getProductId(), event.getTitle());
        productLookupRepository.save(productLookupEntity);
    }
}
