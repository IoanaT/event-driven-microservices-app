package com.microservices.event.orderws.query;

import lombok.Value;

@Value
public class FindOrderQuery {

    private final String orderId;

}
