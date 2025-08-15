# FileUtilityService

A Spring Boot application that provides file utility services with REST API endpoints.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Monitoring](#monitoring)
- [Development](#development)
- [Building for Production](#building-for-production)
- [Contributing](#contributing)

## Overview

FileUtilityService is a RESTful web service built with Spring Boot that provides various file utility operations. The service is designed to be lightweight, efficient, and easy to integrate with other applications.

## Features

- RESTful API endpoints for file operations
- Comprehensive API documentation with Swagger/OpenAPI
- Spring Boot Actuator for monitoring and health checks
- Support for GraalVM native compilation
- Development tools integration with hot reload

## Technology Stack

- **Java**: 24
- **Spring Boot**: 3.5.4
- **Spring Web**: For REST API development
- **Lombok**: For reducing boilerplate code
- **SpringDoc OpenAPI**: For API documentation (Swagger UI)
- **Spring Boot Actuator**: For monitoring and health checks
- **Apache Commons Lang**: For utility functions
- **Maven**: For build management
- **GraalVM**: For native compilation support

## Prerequisites

- Java 24 or higher
- Maven 3.6.0 or higher
- (Optional) GraalVM for native compilation

## Installation

1. Clone the repository:

```bash
git clone https://github.com/yourusername/FileUtilityService.git
cd FileUtilityService
```

2. Build the project:

```bash
mvn clean install
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on port 8080 by default.

## API Documentation

The FileUtilityService provides comprehensive API documentation using SpringDoc OpenAPI (Swagger UI). This documentation includes detailed descriptions, request/response schemas, and examples for all API endpoints.

### Accessing the API Documentation

1. Start the application
2. Navigate to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) in your web browser

### Features of the API Documentation

- **Interactive Documentation**: Test API endpoints directly from the browser
- **Detailed Endpoint Information**: Each endpoint includes descriptions, parameters, request bodies, and response schemas
- **Model Schemas**: Complete documentation of all data models used in the API
- **Response Examples**: Sample responses for each endpoint
- **Error Responses**: Documentation of possible error responses and their meanings

### API Endpoints

The API is organized into the following categories:

1. **File Operations**
   - List all files
   - Upload files
   - Download a file
   - Download a resized image file
   - Delete files

2. **User Operations**
   - Get user details

3. **Application**
   - Hello endpoint (health check)

## Monitoring

The application includes Spring Boot Actuator for monitoring and health checks. Actuator endpoints are available at `/actuator`.

## Development

This project uses Spring Boot DevTools for hot reloading during development.

## Building for Production

To build a production-ready JAR file:

```bash
mvn clean package
```

To build a native executable using GraalVM:

```bash
mvn -Pnative native:compile
```