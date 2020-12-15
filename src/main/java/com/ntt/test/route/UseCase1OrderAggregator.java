package com.ntt.test.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
/*

Create route to read Order.xml file, split each row using xpath and aggregate the total amount and quantity.
The final total amount and quantity should be written to data/usecase1/out folder. At the end you must write an unit
test case to validate the aggregation logic.

Input           : Refer data/sample/order.xml
Expected output : Refer data/sample/order-output.xml

 */
@Component
public class UseCase1OrderAggregator extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file://data/usecase1/in")
                .log(">>> ${body}");
    }
}
