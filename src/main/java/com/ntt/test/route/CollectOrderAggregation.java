package com.ntt.test.route;

import com.ntt.test.model.usecase1.OrderItem;
import com.ntt.test.model.usecase1.OrderSummary;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class CollectOrderAggregation implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        OrderItem orderItem = newExchange.getIn().getBody(OrderItem.class);
        if (oldExchange == null) {
            OrderSummary summary = new OrderSummary();
            summary.totalAmount = Integer.parseInt(orderItem.amount);
            summary.totalOrders = 1;
            summary.totalQty = Integer.parseInt(orderItem.orderItemQty);
            newExchange.getIn().setBody(summary);
            return newExchange;
        }
        OrderSummary summary = oldExchange.getIn().getBody(OrderSummary.class);
        summary.totalAmount += Integer.parseInt(orderItem.amount);
        summary.totalOrders++;
        summary.totalQty += Integer.parseInt(orderItem.orderItemQty);
        return oldExchange;
    }
}
