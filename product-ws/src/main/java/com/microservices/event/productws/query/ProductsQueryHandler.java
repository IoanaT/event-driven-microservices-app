package com.microservices.event.productws.query;

import com.microservices.event.productws.core.data.ProductEntity;
import com.microservices.event.productws.core.data.ProductRepository;
import com.microservices.event.productws.query.rest.ProductRestModel;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProductsQueryHandler {

    private final ProductRepository productRepository;

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductsQuery findProductsQuery) {
        List<ProductEntity> storedProducts = productRepository.findAll();
        List<ProductRestModel> productsRest = storedProducts.stream().map(storedProduct -> {
            ProductRestModel productRest = new ProductRestModel();
            BeanUtils.copyProperties(storedProduct, productRest);
            return productRest;
        }).collect(Collectors.toList());
        return productsRest;
    }
}
