# Smart Campus API

## Project Overview

This project is a RESTful API built with JAX-RS (Jersey 2.34, `javax` namespace) to manage a Smart Campus infrastructure. It allows administrators to manage physical spaces (Rooms), deploy IoT hardware (Sensors), and track historical data (Sensor Readings).

The application strictly adheres to the coursework constraints:

- **No Spring Boot**: Built entirely using standard JAX-RS specifications.
- **No External Database**: Utilizes an in-memory `ConcurrentHashMap` (`DataStore.java`) for thread-safe data persistence.
- **Semantic Error Handling**: Uses custom Exceptions and ExceptionMappers to return accurate HTTP status codes.

## Architecture

The source code is organized into a layered, enterprise-grade architecture:

- `model`: Encapsulates core data POJOs (`Room`, `Sensor`, `SensorReading`).
- `store`: Manages in-memory data structures.
- `resource`: Contains JAX-RS controllers defining the API endpoints.
- `exception`: Houses business logic exceptions and HTTP status mappers.
- `filter`: Contains observability interceptors for request/response logging.

## Build and Run Instructions

To build and run this API locally:

1. Ensure you have **Java 17** and **Maven** installed.
2. Open your terminal in the root directory of the project (where `pom.xml` is located).
3. Run the following Maven command to resolve dependencies and build the project:
   ```bash
   mvn clean install
   ```
