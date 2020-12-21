package com.ntt.test.route;

import com.ntt.test.model.usecase2.Booking;
import com.ntt.test.model.usecase2.BookingResponse;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jacksonxml.JacksonXMLDataFormat;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

@Component
public class UseCase2FlightBooking extends RouteBuilder {

    /** error response body */
    private static final String ERROR_ANSWER = "ERROR";

    @Value("${config.usecase2.port}")
    private int port;

    /** providers URI */
    @Value("${config.usecase2.provider}")
    private String[] providers;

    @Override
    public void configure() throws Exception {

        onException(HttpOperationFailedException.class, ConnectException.class)
                .maximumRedeliveries(2) // maximum redelivers
                .handled(true) // make it handled
                .setBody(constant(ERROR_ANSWER)); // set body as an error

        // for rest use undertow
        restConfiguration()
                .component("undertow").port(port);

        // xml
        JacksonXMLDataFormat bookingRequestFormat = new JacksonXMLDataFormat();
        bookingRequestFormat.setUnmarshalType(Booking.class);
        JacksonXMLDataFormat bookingResponseFormat = new JacksonXMLDataFormat();
        bookingResponseFormat.setUnmarshalType(BookingResponse.class);

        // TODO #1 receive POST /camel-integration/flights/search request with XML payload
        rest("/camel-integration/flights")
                .post("search").produces("application/xml")
                .to("direct:searchFlight");

        // TODO #2 convert into JSON format
        from("direct:searchFlight").routeId("flightSearchRoute")
                .unmarshal(bookingRequestFormat) // unmarshall to POJO
                // TODO #3 send requests to providers in parallel
                // TODO #4 choose response with lowest amount field
                .split().method(new ProviderMessageGenerator(providers), "messages") // generate message for each provider
                .parallelProcessing() // use parallel processing
                .aggregationStrategy(new ProviderResponseAggregator()) // collect provider responses
                  .to("direct:providerCall") // call provider
                .end()
                .log(">>> ${body}")
                // TODO #5 marshal response and return
                .marshal(bookingResponseFormat)
                .log("${body}");

        // get provider price
        from("direct:providerCall")
                .log("CALL PROVIDER: ${header." + ProviderMessageGenerator.PROVIDER_URI_HEADER + "}")
                .marshal().json(JsonLibrary.Jackson) // marshal to JSON
                .setHeader(Exchange.HTTP_METHOD, constant("POST")) // POST
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json")) // json
                .toD("undertow:${header." + ProviderMessageGenerator.PROVIDER_URI_HEADER + "}") // call provider
                .unmarshal().json(JsonLibrary.Jackson, BookingResponse.class) // unmarshall to POJO
                .log("PROVIDER RESPONSE: ${body}");
    }

    /**
     * Aggregates provider responses.
     */
    private class ProviderResponseAggregator implements AggregationStrategy {

        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            BookingResponse newResponse;
            if (!ERROR_ANSWER.equals(newExchange.getIn().getBody())) {
                newResponse = newExchange.getIn().getBody(BookingResponse.class);
            } else {
                // empty response on error
                newResponse = new BookingResponse();
            }
            if (oldExchange == null) {
                // first non-error response
                try {
                    Integer.parseInt(newResponse.amount);
                } catch (NumberFormatException ex) {
                    return null;
                }
                return newExchange;
            }
            // choose response with fair price
            BookingResponse oldResponse = oldExchange.getIn().getBody(BookingResponse.class);
            int oldAmount = Integer.parseInt(oldResponse.amount);
            try {
                int newAmount = Integer.parseInt(newResponse.amount);
                if (newAmount > oldAmount) {
                    return oldExchange;
                }
            } catch (NumberFormatException ex) {
                return oldExchange;
            }
            return newExchange;
        }
    }
}
