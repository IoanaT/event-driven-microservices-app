package com.microservices.event.orderws.saga;

import com.microservices.event.core.commands.CancelProductReservationCommand;
import com.microservices.event.core.commands.ProcessPaymentCommand;
import com.microservices.event.core.commands.ReserveProductCommand;
import com.microservices.event.core.events.OrderApprovedEvent;
import com.microservices.event.core.events.PaymentProcessedEvent;
import com.microservices.event.core.events.ProductReservationCancelledEvent;
import com.microservices.event.core.events.ProductReservedEvent;
import com.microservices.event.core.model.User;
import com.microservices.event.core.query.FetchUserPaymentDetailsQuery;
import com.microservices.event.orderws.command.ApproveOrderCommand;
import com.microservices.event.orderws.core.events.OrderCreatedEvent;
import com.microservices.event.orderws.command.RejectOrderCommand;
import com.microservices.event.orderws.core.events.OrderRejectedEvent;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    private final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";

    private String scheduleId;

    public OrderSaga() {

    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();
        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
            @Override
            public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
                                 CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    //start a compensating transaction
                }
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        //process user payment
        LOGGER.info("ProductReservedEvent is called for productId: " + productReservedEvent.getProductId() +
                " and orderId: " + productReservedEvent.getOrderId());
        FetchUserPaymentDetailsQuery query = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway.query(query, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            //start compensating transaction
            cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }
        if (userPaymentDetails == null) {
            //start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not fetch user payment details.");
            return;
        }
        LOGGER.info("Successfully user payment details for user: " + userPaymentDetails.getFirstName());

        scheduleId = deadlineManager.schedule(Duration.of(120, ChronoUnit.SECONDS),
                PAYMENT_PROCESSING_TIMEOUT_DEADLINE, productReservedEvent);

        ProcessPaymentCommand proccessPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;
        try {
            result = commandGateway.sendAndWait(proccessPaymentCommand, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            //start compensating transaction
            cancelProductReservation(productReservedEvent, e.getMessage());
            return;
        }
        if (result == null) {
            LOGGER.info("The ProcessPaymentCommand resulted in NULL. Initiating a compensating transaction");
            //start compensating transaction
            cancelProductReservation(productReservedEvent, "Could not process user payment with provided payment details.");
        }
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {

        cancelDeadline();

        CancelProductReservationCommand publishProductReservationCommand =
                CancelProductReservationCommand.builder()
                        .orderId(productReservedEvent.getOrderId())
                        .productId(productReservedEvent.getProductId())
                        .quantity(productReservedEvent.getQuantity())
                        .userId(productReservedEvent.getUserId())
                        .reason(reason)
                        .build();

        commandGateway.send(publishProductReservationCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        cancelDeadline();
        //send an ApprovedOrderCommand
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());
        commandGateway.send(approveOrderCommand);
    }

    private void cancelDeadline() {
        if (scheduleId != null) {
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
            scheduleId = null;
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        LOGGER.info("Order is approved. Order Saga is complete for orderId: \" + orderApprovedEvent.getOrderId());");
//        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        // Create and send a RejectOrderCommand
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(productReservationCancelledEvent.getOrderId(),
                productReservationCancelledEvent.getReason());

        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent orderRejectedEvent) {
        LOGGER.info("Successfully rejected order with id " + orderRejectedEvent.getOrderId());
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        LOGGER.info("Payment processing deadline took place. Sending a compensating command to cancel the product reservation");
        cancelProductReservation(productReservedEvent, "Payment timeout");
    }
}
