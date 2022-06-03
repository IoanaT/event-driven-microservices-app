package com.microservices.event.paymentsws.command;

import com.google.common.base.Strings;
import com.microservices.event.core.commands.ProcessPaymentCommand;
import com.microservices.event.core.events.PaymentProcessedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import java.util.Objects;

@Aggregate
@NoArgsConstructor
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;

    private String orderId;

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
        if (Objects.isNull(processPaymentCommand.getPaymentDetails())) {
            throw new IllegalArgumentException("Missing payment details");
        }
        if (Strings.isNullOrEmpty(processPaymentCommand.getPaymentId())) {
            throw new IllegalArgumentException("Missing paymentId");
        }
        if (Strings.isNullOrEmpty(processPaymentCommand.getOrderId())) {
            throw new IllegalArgumentException("Missing orderId");
        }
        PaymentProcessedEvent paymentProcessedEvent = PaymentProcessedEvent.builder()
                .orderId(processPaymentCommand.getOrderId())
                .paymentId(processPaymentCommand.getPaymentId())
                .build();
        AggregateLifecycle.apply(paymentProcessedEvent);
    }

    @EventSourcingHandler
    protected void on(PaymentProcessedEvent paymentProcessedEvent) {
        this.orderId = paymentProcessedEvent.getOrderId();
        this.paymentId = paymentProcessedEvent.getPaymentId();
    }
}
