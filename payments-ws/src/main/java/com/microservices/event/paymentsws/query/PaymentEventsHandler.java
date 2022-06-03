package com.microservices.event.paymentsws.query;

import com.microservices.event.core.events.PaymentProcessedEvent;
import com.microservices.event.paymentsws.data.PaymentEntity;
import com.microservices.event.paymentsws.data.PaymentRepository;
import lombok.AllArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PaymentEventsHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(PaymentEventsHandler.class);
    private final PaymentRepository paymentRepository;

    @EventHandler
    public void on(PaymentProcessedEvent event){
        LOGGER.info("PaymentProcessedEvent is called for orderId: " + event.getOrderId());

        PaymentEntity paymentEntity = new PaymentEntity();
        BeanUtils.copyProperties(event, paymentEntity);

        paymentRepository.save(paymentEntity);
    }
}
