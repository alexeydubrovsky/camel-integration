package com.ntt.test;

import com.ntt.test.model.usecase2.Booking;
import com.ntt.test.model.usecase2.BookingResponse;
import com.ntt.test.route.UseCase2FlightBooking;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(CamelSpringBootRunner.class)
@SpringBootApplication
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@UseAdviceWith
@ActiveProfiles("Test")
public class UseCase2FlightBookingTest {
    @Autowired
    private CamelContext context;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${config.usecase2.port}")
    private int port;

    @Value("${config.usecase2.provider}")
    private String[] providers;

    private static final String REQUEST1 = "<booking>\n" +
            "            <from>DAL</from>\n" +
            "            <to>ATL</to>\n" +
            "            <date>12-15-2020</date>\n" +
            "        </booking>\n";

    @Before
    public void mockEndpoints() throws Exception {
    }

    @Test
    public void firstTest() throws Exception {
        // start camel context
        context.start();

        context.addRoutes(new RouteBuilder() {
            @Override public void configure() throws Exception {
                rest("/provider1")
                        .post("search")
                        .produces("application/json")
                        .to("direct:fakeProvider");
                rest("/provider2")
                        .post("search")
                        .produces("application/json")
                        .to("direct:fakeProvider");

                from("direct:fakeProvider")
                        .unmarshal().json(JsonLibrary.Jackson, Booking.class)
                        .process(new FakeProviderResponseProcessor())
                        .marshal().json(JsonLibrary.Jackson) // marshal to JSON
                        .log("FAKE PROVIDER");
            }
        });

        String rootUrl = "http://localhost:" + port + "/camel-integration";

        ResponseEntity<String> response = restTemplate.postForEntity(
                rootUrl + "/flights/search",
                REQUEST1,
                String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        String responseStr = response.getBody();
        Document doc = loadDom(responseStr);
        String amount = getText(doc, "/booking/amount/text()");
        assertEquals("905", amount);
        String provider = getText(doc, "booking/provider/text()");
        assertEquals("provider42", provider);
        System.err.println("RESPONSE: " + responseStr);
    }

    private class FakeProviderResponseProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Booking request = exchange.getIn().getBody(Booking.class);
            BookingResponse response = new BookingResponse();
            response.from = request.from;
            response.to = request.to;
            response.date = request.date;
            if (exchange.getFromEndpoint().getEndpointKey().contains("provider1")) {
                response.amount = "1000";
                response.provider = "provider43";
            } else {
                response.amount = "905";
                response.provider = "provider42";
            }
            exchange.getIn().setBody(response);
        }
    }

    private Document loadDom(String xmlStr) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlStr)));
    }

    private String getText(Node node, String expression) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        return xPath.compile(expression).evaluate(node);
    }

}
