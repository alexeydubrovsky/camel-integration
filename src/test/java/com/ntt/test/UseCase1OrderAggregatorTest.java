/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.ntt.test;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import static org.junit.Assert.assertFalse;

@RunWith(CamelSpringBootRunner.class)
@SpringBootApplication
@SpringBootTest(classes = Application.class)
@UseAdviceWith
public class UseCase1OrderAggregatorTest {

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate template;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void mockEndpoints() throws Exception {
        System.out.println("Starting Test");
        context.getRouteDefinition("orderAggregatorRoute").adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:input");
                weaveByToUri("{{config.usecase1.output}}").replace().to("mock:orderOutputEndpoint");
            }
        });

    }

    @Test
    public void testOrderAggregation() throws Exception {
        context.start();
        MockEndpoint orderOutputEndpoint = MockEndpoint.resolve(context, "mock:orderOutputEndpoint");

        orderOutputEndpoint.setExpectedMessageCount(1);

        String inputMessage = "<order> <orderItems> <orderItem> <orderId>1001</orderId> <shippingAddress> <zipCode>99999</zipCode> </shippingAddress> <orderItemQty>1</orderItemQty> <amount>100</amount> </orderItem> <orderItem> <orderId>1002</orderId> <shippingAddress> <zipCode>99993</zipCode> </shippingAddress> <orderItemQty>10</orderItemQty> <amount>1000</amount> </orderItem> </orderItems> </order>";
        String expectedMessage = "<OrderSummary> <totalOrders>2</totalOrders> <totalQty>11</totalQty> <totalAmount>1100</totalAmount> </OrderSummary>";

        template.sendBody("direct:input", inputMessage);

        String actualMessage = orderOutputEndpoint.getExchanges().get(0).getIn().getBody(String.class);

        Diff myDiff = DiffBuilder.compare(actualMessage)
                .withTest(expectedMessage)
                .checkForSimilar()
                .build();

        assertFalse(myDiff.toString(), myDiff.hasDifferences());

        System.out.println("***********************Test Results**************************");

        orderOutputEndpoint.assertIsSatisfied();
        context.stop();
    }
}
