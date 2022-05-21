package com.microservices.event.productws.rest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductsController {

    @PostMapping
    public String createProduct() {
        return "HTTP POST HANDLED";
    }

    @GetMapping
    public String getProduct() {
        return "HTTP GET HANDLED";
    }

    @PutMapping
    public String updateProduct() {
        return "HTTP PUT HANDLED";
    }

    @DeleteMapping
    public String deleteProduct() {
        return "HTTP DELETE HANDLED";
    }
}