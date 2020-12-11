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
    Expose a REST API using Apache Camel (you can use any camel rest implementations) to accept the following XML.
    
    2.1: Develop an API to expose the below API. 
     
    POST /camel-integration/flights/search
    Body :
    <booking>
        <from>location</from>
        <to>location</to>
        <date>mm-dd-yyyy</date>
    </booking>    
    
    2.2: Upon receiving the request, convert the request to JSON and send it to queue (use Seda queue for this use case)
    
    2.3: Create another route to read the booking json message from queue and multicast the message to 3 different airline providers
         then aggregate the response to find the lowest fair price. You must mock the airline providers endpoints. 
         
         Sample Response from Airline Provider will look like this 
                {
                    "from": "location",
                    "to": "location",
                    "date": "mm-dd-yyyy",
                    "amount": 500                
                }
    2.4: The final response with lowest fair should be printed in the console log. 
                
Use com.ntt.test.route.UseCase2FlightBooking class for implementation.    
    
    
    
