# Assignment 
### Objective: 
* Design and implement a solution for exchanging trailers using java, spring boot and microservice.

### Requirements:
* Input:
  * A transport order using public API that includes:
    * Starting location (geolocation)
    * Endpoint location (geolocation)

### Output:
* Split the transport order into two legs and assign each leg to different carriers.
* Retrieve the transport order status from the different carriers and inform the real-time status to the other carriers delivering the last leg of the same transport order

### Predefined Data:
* Exchange locations
* Carriers
* Transport buyers

# Trailer Exchange Solution [PoC]
The trailer-exchange-poc repository is an experimental attempt to setup self-contained microservice modules with Spring Modulith.

# Project Modules
The trailer-exchange-poc consists of tree modules
1. dependencies - provides shared dependencies (POM)
2. modulith - provides Spring Modulith (microservice modules)
3. runtime - application runtime environment
   * standalone - runs system as a single unit (local/dev/test)

# Micro-services (modulith)
The modulith module contains microservices for carriers, locations, notifications and transport orders. 
* Each module is a self-contained microservice that may 
    * publishes events to trigger processing in other container modules
    * expose services (REST APIs) to external consumers
    * fetch external data from other modules (REST client)
    * stores internal data (JPA)

# Supported Features
* Predefined Data (Locations, Carriers, Tranport Buyers, etc)
  * is loaded from the following (the standalone runtime environment)
    * org.trailerexchange.data.PredefinedDataLoader
    * org.trailerexchange.data.PredefinedDataFixtures
* Notifications (Spring Event Publisher)
  * configured in application.yml
* API Endpoints (Spring Boot)
* Test Containers and SpringBootTest (to run flow)
* Modulith Application
  * running with Spring Boot, PostgreSQL, Kafka

# Limitations
* Functional
  * Carrier selection logic has not been implemented (returns same carrier for both legs)
  * Tried to add Swagger UI, but it doesn't load properly
    * if configured correctly, it should show at http://localhost:8080/swagger-ui/index.html 
* Quality
  * No unit tests (only an experimental implementation)
  * No security mechanism
  * No exception handling
  * Error-prone implementation
  * No user interface, only a component test (and REST APIs)
  * Buggy and redundant code with lots or comments
  * No Feign clients ;/
  * No queues (only a simple notification mechanism)

# Transport Order Process

Step 1: The transport order is split into two legs:
* Leg 1: From Goteborg to Sarpsborg, handled by Carrier A (DbSchenker).
* Leg 2: From Sarpsborg to Oslo, handled by Carrier B (Posten Norge).

Step 2: Each carrier is informed of their respective leg and the exchange location.

Step 3: Carrier A updates the system with the status when the trailer arrives at Sarpsborg.
* This information is passed on to Carrier B, allowing it to proceed with the final leg.

Step 4:
* When the transport of the two legs is complete (TransportLegStatus=COMPLETED), the status for the whole transport order is complete (TransportOrderStatus=COMPLETED).

## Internal Modules
### Transport orders
* [TransportOrderController.java](modulith/src/main/java/org/trailerexchange/transportorders/TransportOrderController.java)
  * creates transport order
  * splits order into two legs
  * assigns a carrier each leg
  * notifies carriers by publishing an event to the TransportOrderProcessor (in the notifications package)
### Notifications
* listens to and manages transport order status changes
* co-ordinates notifications to the carriers and transport orders modules for service execution
### Carriers
* identifies which carrier to assign to a route (transport leg)
* emulates transport
### Locations
* helps find exchange location

# Runtime

Build, test and install
```
mvn clean install
```

# Run Test 
- to verify the transport order process:
```
runtime/standalone/org.trailerexchange.events.externalization.TransportOrderSplitLiveTest
```
- read the logs to observe the steps executed


# Start application

Runs in standlone-mode with the following command:
```
mvn -f runtime/standalone/pom.xml spring-boot:run
```
(however, it doesn't have any UI)

