package com.ntt.test.route;

import com.ntt.test.model.usecase2.Booking;
import org.apache.camel.Body;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates a message for each provider.
 */
public class ProviderMessageGenerator {

    static final String PROVIDER_URI_HEADER = "providerUri";

    private final String[] providers;

    public ProviderMessageGenerator(String[] providers) {
        this.providers = providers;
    }

    public List<Message> messages(@Body Booking bookingRequest, CamelContext camelContext) {
        List<Message> messageList = new ArrayList<>(providers.length);
        for (int i = 0; i < providers.length; i++) {
            DefaultMessage m = new DefaultMessage(camelContext);
            m.setBody(bookingRequest);
            m.setHeader(PROVIDER_URI_HEADER, providers[i]);
            messageList.add(m);
        }
        return messageList;
    }
}
