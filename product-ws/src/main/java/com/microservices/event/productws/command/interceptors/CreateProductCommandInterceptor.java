package com.microservices.event.productws.command.interceptors;

import com.microservices.event.productws.command.commands.CreateProductCommand;
import com.microservices.event.productws.core.data.ProductLookupEntity;
import com.microservices.event.productws.core.data.ProductLookupRepository;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@Component
@AllArgsConstructor
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    private final ProductLookupRepository productLookupRepository;

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {

        return (index, command) -> {

            LOGGER.info("Intercepted command: " + command.getPayloadType());

            if (CreateProductCommand.class.equals(command.getPayloadType())) {

                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();

                ProductLookupEntity productLookupEntity = productLookupRepository.findByProductIdOrTitle(
                        createProductCommand.getProductId(), createProductCommand.getTitle());
                if (!Objects.isNull(productLookupEntity)) {
                    throw new IllegalStateException(String.format(
                            "Product with productId %s or title %s already exists",
                            createProductCommand.getProductId(), createProductCommand.getTitle()));
                }
            }
            return command;
        };
    }

}

