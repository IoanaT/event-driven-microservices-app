package com.microservices.event.orderws.orderws.query;

import com.microservices.event.orderws.orderws.core.data.OrderEntity;
import com.microservices.event.orderws.orderws.core.data.OrdersRepository;
import com.microservices.event.orderws.orderws.core.events.OrderCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("order-group")
public class OrderEventsHandler {

        private final OrdersRepository ordersRepository;

        public OrderEventsHandler(OrdersRepository ordersRepository) {
            this.ordersRepository = ordersRepository;
        }

        @EventHandler
        public void on(OrderCreatedEvent event) throws Exception {
            OrderEntity orderEntity = new OrderEntity();
            BeanUtils.copyProperties(event, orderEntity);

            this.ordersRepository.save(orderEntity);
        }

    }