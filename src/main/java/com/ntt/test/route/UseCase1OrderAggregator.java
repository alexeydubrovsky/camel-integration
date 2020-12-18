package com.ntt.test.route;

import org.apache.camel.builder.RouteBuilder;
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
        from("{{config.usecase1.input}}").routeId("orderAggregatorRoute")
                .log(">>> ${body}")
                // TODO #1: Implement Split and Aggregate
                // TODO #2: Prepare output POJO
                // TODO #3: Convert POJO to XML
                .to("{{config.usecase1.output}}");
        }
}
