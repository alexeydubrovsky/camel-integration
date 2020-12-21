package com.ntt.test.route;

import com.ntt.test.model.usecase1.OrderItem;
import com.ntt.test.model.usecase1.OrderSummary;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jacksonxml.JacksonXMLDataFormat;
import org.springframework.stereotype.Component;

/*

Create route to read Order.xml file, split each row and aggregate the total amount and quantity.
The final total amount and quantity should be written as xml file in data/usecase1/out folder.
Make sure UseCase1OrderAggregatorTest is successful after implementing this use case.

Input           : Refer data/sample/order.xml (POJO: com.ntt.test.model.usecase1.Order.java)
Expected output : Refer data/sample/order-output.xml



 */
@Component
public class UseCase1OrderAggregator extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        JacksonXMLDataFormat format = new JacksonXMLDataFormat();
        format.setUnmarshalType(OrderItem.class);
        from("{{config.usecase1.input}}").routeId("orderAggregatorRoute")
                .log(">>> ${body}")
                // TODO #1: Implement Split and Aggregate
                // TODO #2: Prepare output POJO
                .split().tokenizeXML("orderItem").aggregationStrategy(new CollectOrderAggregation())
                .unmarshal(format) // convert to OrderItem
                .end() // split-aggregation
                .log(">>> ${body}")
                // TODO #3: Convert POJO to XML
                .marshal().jacksonxml(OrderSummary.class, false)
                .log(">> result ${body}")
                .to("{{config.usecase1.output}}");
        }
}
