package com.ntt.test.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class UseCase1OrderAggregator extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file://data/usecase1/in")
                .log(">>> ${body}");
    }
}
