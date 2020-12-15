"# camel-integration" 

# Integration using Apache Camel

# Prerequisite

    Java 1.8, Maven 
    IDE (Eclipse, IntelliJ or VSCode)    
    
# Setup 

1. Checkout code from https://github.com/writemevenkat/camel-integration.git and create a new branch called develop. 
2. Import camel-integration app into your IDE and run the bootstrapped application. 
    Run -> com.ntt.test.Application
     
    
# Use Case #1: 
    Create route to read Order.xml file, split each row using xpath and aggregate the total amount and quantity. 
    The final total amount and quantity should be written to data/case/out folder. At the end you must write an unit
    test case to validate the aggregation logic. 
    
    Input           : Refer data/sample/order.xml
    Expected output : Refer data/sample/order-output.xml
 
Use com.ntt.test.route.UseCase1OrderAggregator class for implementation. 

# Use Case #2:
    Expose a REST API using Apache Camel (you can use any Came REST implementations i.e CXF/Servlet/Undertow) 
    to accept the following XML.
    
    2.1: Develop an API to expose the below API. 
     
    POST /camel-integration/flights/search
    Body :
    <booking>
        <from>location</from>
        <to>location</to>
        <date>mm-dd-yyyy</date>
    </booking>    
    
    2.2: Upon receiving the request, convert received XML to JSON request then multicast this message to 3 different airline
         providers. The response response from the providers should be aggregated to find the lowest fair price. 
         Note: You must mock the airline providers endpoints. 
         
         Sample Mock Response from airline provider will look like this 
                {
                    "from": "location",
                    "to": "location",
                    "date": "mm-dd-yyyy",
                    "amount": 500                
                }
    2.4: The final response XML with lowest fair should be sent back to the consumer. 
        <booking>
            <from>location</from>
            <to>location</to>
            <date>mm-dd-yyyy</date>
            <amount></amount>
        </booking>
                
Use com.ntt.test.route.UseCase2FlightBooking class for implementation.    
    
    
    
