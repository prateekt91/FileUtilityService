# GraalVM Spring Boot Demo

A Spring Boot application demonstrating GraalVM native image capabilities with basic REST endpoints and health monitoring.

## Prerequisites

- JDK 17 or later
- GraalVM
- Maven 3.6+
- Spring Boot 3.x

## Features

- Spring Boot REST endpoints
- Actuator health monitoring
- GraalVM native image support

## Configuration

The application uses the following key configurations in `application.properties`:

```properties
spring.application.name=GraalVM_Spring
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.endpoints.web.base-path=/actuator