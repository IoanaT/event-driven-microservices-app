package com.microservices.event.productws.rest;

import com.microservices.event.productws.command.CreateProductCommand;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductsController {

    private final Environment env;
    private final CommandGateway commandGateway;

    @PostMapping
    public String createProduct(@RequestBody CreateProductRestModel createProductRestModel) {
        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .price(createProductRestModel.getPrice())
                .title(createProductRestModel.getTitle())
                .quantity(createProductRestModel.getQuantity())
                .productId(UUID.randomUUID().toString()).build();
        String returnValue;
        try {
            returnValue = commandGateway.sendAndWait(createProductCommand);
        } catch (Exception e) {
            returnValue = e.getLocalizedMessage();
        }
        return returnValue;
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