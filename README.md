# Smart Campus API

**Student Name:** Balasuriyage Don Chamod Bevan Steferd  
**Student ID:** 20240913  
**Module:** Client-Server Architectures  
**Date:** April 24, 2026

---

## Project Overview

This project is a RESTful API built with JAX-RS (Jersey 2.34, `javax` namespace) to manage a Smart Campus infrastructure.

### Architecture (API Design)

- **model**: Core data POJOs.
- **store**: Thread-safe `DataStore` using `ConcurrentHashMap`.
- **resource**: JAX-RS controllers for API endpoints.
- **exception**: Custom ExceptionMappers for HTTP status codes.
- **filter**: Logging interceptors for requests/responses.

---

## Build and Run Instructions

1. Ensure you have **Java 17** and **Maven** installed.
2. Open your terminal in the project root.
3. **Build the project:**

```bash
mvn clean install
```

4. Start the server:

```bash
mvn exec:java -Dexec.mainClass="Main"
```

5. The API will be locally accessible at: `http://localhost:8080/api/v1/`

## Sample cURL Commands

```bash
# Test Discovery (GET)
curl -X GET http://localhost:8080/api/v1

# Create Room (POST)
curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\": \"LAB-101\", \"name\": \"Computer Science Lab\", \"capacity\": 30}"

# Get All Rooms (GET)
curl -X GET http://localhost:8080/api/v1/rooms

# Register Sensor (POST)
curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\": \"CO2-101\", \"type\": \"Air Quality\", \"status\": \"ACTIVE\", \"roomId\": \"LAB-101\"}"

# Post Reading (POST)
curl -X POST http://localhost:8080/api/v1/sensors/CO2-101/read -H "Content-Type: application/json" -d "{\"value\": 415.5}"
```

## Conceptual Report

Question 01 : Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

For each new HTTP request coming to JAX-RS the resource class is instantiated from its corresponding class file and stored as a new instance (no singletons) this causes all requests made to the server at the same time will attempt to access the data layer concurrently. Without concurrent data structures to support these requests I had to very carefully ensure that any of my in-memory data structure would not be corrupted or throw concurrentModificationExceptions. A ConcurrentHashMap was used since it automatically handles concurrent read/write without any manually coded synchronization blocks.

Question 02 : Why is the provision of “Hypermedia” (links and navigation within responses considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

Due to its ability to allow the API to send navigation links within the response to client applications, using HATEOAS is a best practice for using RESTful APIs. By allowing developers of the client application to dynamically navigate through links from the server as opposed to having to hard-code the URLs in static documents, the client application will not fail if the routing structure of the API is changed at a future date.

Question 03 : When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

Passing back only the room IDs will greatly reduce the size of the response payload and save on network bandwidth. Unfortunately, this solution creates the "N+1 query problem". Therefore, the client will have to perform a large number of additional API calls simply to retrieve the details for each room individually. I chose to return back all the details for a room in a single response instead. Although it does result in slightly more bandwidth being used up front, by passing back all the details for a room to the client in one request, the client is able to load everything into memory at once allowing the UI to be rendered much quicker.

Question 04 : Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple time.

My operation when performing a DELETE action is idempotent. The first delete request will successfully delete the room from the ConcurrentHashMap when a client accidentally sends identical DELETE requests multiple times. Each subsequent delete request will simply attempt to delete something that does not exist. The server state does not change and the room remains deleted; however, the server may return a “404 Not Found” status code for the second attempt.

Question 05 : We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

The @Consumes(MediaType.APPLICATION_JSON) annotation operates as an exact match filter. The framework will intercept the request before it reaches my Java method if a client attempts to send data that is not formatted correctly (e.g., text/plain, application/xml) when working with the JAX-RS system. Therefore, it will automatically reject the invalid payload and send an HTTP 415 Unsupported Media Type error. As a result, JAX-RS will prevent my server from trying to parse an invalid data payload.

Question 06 : You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why
is the query parameter approach generally considered superior for filtering and searching collections?

Path parameters identify uniquely identifiable (or individually designated) resources and allow you to access a given resource directly. Query parameters allow for the optional filtering or sorting of collections. When you use a path such as /api/v1/sensors/type/CO2, it gives the impression that "type" is a sub-resource of the resource CO2, which is inaccurate semantically. The query parameter method using ?type=CO2 is more semantic, and is considered a more acceptable method to follow standard RESTful practices for filtering.

Question 07 : Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

The Sub-Resource Locator pattern is highly beneficial in maintaining a clean code base. By separating logic rather than forcing all logic for both Sensors and their Historical Readings into one large SensorResource' god class. The main class only performs the task of locating a Sensor, then passes the requests to the SensorReadingResource. This modularity allows for a much easier to understand and maintainable code base.

Question 08 : Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

The HTTP response code 404 Not Found indicates that the target URL for an API endpoint does not exist; in this case, the endpoint for an API request made to /api/v1/sensors is highly likely valid with respect to both the URL and the actual JSON content sent from the client (to include correct formatting). The reason for this, however, is that the roomId associated with this JSON does not exist in the system. Therefore, an HTTP response code of 422 Unprocessable Entity would have been more appropriate due to the application logic indicating that the server could process the JSON and understand the content, however the application's logical reference to the roomId does not exist in the application logic or in this case, within the application.

Question 09 : From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

By exposing raw Java stack traces you create an extremely high security risk. Raw Java stack traces expose the inner workings of an API. An attacker can use this information from a stack trace to determine the frameworks that are being used (and the versions of each) as well as the internal package structure of the application. If any of those libraries have an existing known vulnerability, the attacker now knows exactly how to target the server based on the Java stack trace.

Question 10 : Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

By utilizing filters provided by JAX-RS over duplicating code using Logger.info() at both the beginning and end of each method, you are able to centralize all logging functionality, thus leaving the actual business logic free of clutter while also maintaining adherence to the DRY (Don’t Repeat Yourself) principle. Additionally, this approach ensures that all requests/responses will be audited automatically without me having to remember to add logger statements to newly-created endpoints.
