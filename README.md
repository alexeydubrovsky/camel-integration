"# camel-integration" 

# Integration using Apache Camel

# Prerequisite

   - Java 1.8, Maven 
   - IDE (Eclipse, IntelliJ or VSCode)    
    
# Setup 

1. Fork [camel-integration](https://github.com/writemevenkat/camel-integration) code to your personal github account.
  
2. Checkout camel-integration project from your github account and import into your IDE. Application can be invoked locally
   using com.ntt.test.Application class.
   
3. To implement use case #1 and #2, you can refer this documentation (https://access.redhat.com/documentation/en-us/red_hat_fuse/7.5/html/apache_camel_development_guide/index)
  
3. After completing the use case, upload your code and share your github link.       
    
# Use Case #1: 

  Create route to read Order.xml file, split each row and aggregate the total amount and quantity. The final total 
  amount and quantity should be written as xml file in data/usecase1/out folder. Make sure UseCase1OrderAggregatorTest
  is successful after implementing this use case. 
    
    Input           : Refer data/sample/order.xml
    Expected output : Refer data/sample/order-output.xml
 
Use com.ntt.test.route.UseCase1OrderAggregator class for implementation. 

# Use Case #2:
  Develop REST API using Apache Camel to find the lowest airline fare price by integrating with multiple airline providers.
  You should cover integration best practices like exception handling, redelivery, logging etc as part of this use case. 
  
    
  2.1: Expose REST API using Apache Camel (you can use any Camel REST implementations i.e CXF/Servlet/Undertow) 
  to accept the following XML.
    
    POST /camel-integration/flights/search
    
    Body :
    <booking>
        <from>location</from>
        <to>location</to>
        <date>mm-dd-yyyy</date>
    </booking>    
    
  2.2: Upon receiving the request XML, convert received request to JSON and multicast copy of the message 
       to 3 different airline providers parallel. The response from the providers should be aggregated to find the lowest 
       fair price. 
       
       Note: You must mock the airline providers endpoints using another direct route endpoint.
                
       Sample Mock Response from airline provider will look like this 
                {
                    "from": "location",
                    "to": "location",
                    "date": "mm-dd-yyyy",
                    "provider": "delta",
                    "amount": 500                
                }
                
  2.4: The final response XML with the lowest fair should be sent back to the consumer. 
       
       Sample Expected Response: 
       
        <booking>
            <from>DAL</from>
            <to>ATL</to>
            <date>12-15-2020</date>
            <provider>delta</provider>
            <amount>500</amount>
        </booking>
                
Use com.ntt.test.route.UseCase2FlightBooking class for implementation.    
    
    
    
