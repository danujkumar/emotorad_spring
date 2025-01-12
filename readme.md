# Contact Management Service

This service manages user contact information by linking multiple email and phone combinations to a single user identity. It provides a `/identify` endpoint that consolidates and retrieves associated contact information.

## Features

- Create new primary and secondary contact entries.
- Link multiple email and phone numbers.
- Seamlessly transform primary contacts into secondary when new data overlaps.
- Error handling with meaningful responses.

## Prerequisites

- Spring Boot
- Java
- MongoDB

## Installation and Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd emotorad_spring
   ```
2. Set up the necessary environment variables in `application.properties`:
   ```properties
   server.port=8080
   spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster.mongodb.net/dbname
   ```
3. Build the application:
   ```bash
   ./mvnw clean install
   ```
4. Run the application:
   ```bash
   java -jar target/contact-management-service-0.0.1-SNAPSHOT.jar
   ```

## API Usage

### POST `/identify`

#### Request Body

```json
{
  "email": "user@example.com",
  "phone": "1234567890",
  "product": "Product Name"
}
```

#### Response Example

```json
{
  "primaryContactId": "60f7b2c5e3b1a9cde0e6b77d",
  "contactPairs": [
    { "email": "user@example.com", "phone": "1234567890" }
  ],
  "secondaryContactIds": ["60f7b2c5e3b1a9cde0e6b77e"],
  "createdAt": "2025-01-07T10:30:00Z",
  "updatedAt": "2025-01-07T10:30:00Z",
  "deletedAt": null
}
```

## Deployment

The application can be deployed using any cloud service that supports Spring Boot applications. A typical deployment approach is to use Docker for containerization.

### Example Docker Deployment

1. Create a `Dockerfile`:
   ```dockerfile
   FROM openjdk:17-jdk-slim
   ARG JAR_FILE=target/contact-management-service-0.0.1-SNAPSHOT.jar
   COPY ${JAR_FILE} app.jar
   ENTRYPOINT ["java", "-jar", "/app.jar"]
   ```
2. Build the Docker image:
   ```bash
   docker build -t contact-management-service .
   ```
3. Run the Docker container:
   ```bash
   docker run -p 8080:8080 contact-management-service
   ```

## Running Tests

1. Run unit tests using Maven:
   ```bash
   ./mvnw test
   ```

