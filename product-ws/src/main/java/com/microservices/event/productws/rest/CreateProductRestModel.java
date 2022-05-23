package com.microservices.event.productws.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
public class CreateProductRestModel {
    @Getter
    @Setter
    private  String title;
    @Getter
    @Setter
    private  BigDecimal price;
    @Getter
    @Setter
    private  Integer quantity;
}
