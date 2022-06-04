package com.microservices.event.orderws.query;

import com.microservices.event.core.model.OrderSummary;
import com.microservices.event.orderws.core.data.OrderEntity;
import com.microservices.event.orderws.core.data.OrdersRepository;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderQueriesHandler {

    private OrdersRepository ordersRepository;

    @QueryHandler
    public OrderSummary findOrderQuery(FindOrderQuery findOrderQuery) {
        OrderEntity orderEntity = ordersRepository.findByOrderId(findOrderQuery.getOrderId());
        return new OrderSummary(orderEntity.getOrderId(),
                orderEntity.getOrderStatus(), "");
    }

}
