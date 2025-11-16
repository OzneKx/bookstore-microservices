# 📚 Bookstore Microservices

Microservices architecture for a digital
bookstore, built with **Java 21**, **Spring Boot 3**, **Spring Cloud**,
**Docker**, **RabbitMQ**, and **PostgreSQL**.

This project demonstrates JWT-based
authentication, message-driven communication, API gateway routing,
service discovery, and  REST APIs with Swagger/OpenAPI
documentation.

------------------------------------------------------------------------

## 🚀 Key Features

-   **Microservices architecture** (Auth, Catalog, Order, Gateway,
    Eureka)
-   **JWT Authentication**
    -   Auth-Service generates tokens
    -   Gateway-Service validates tokens
-   **Service Discovery** using Eureka
-   **API Gateway** routing and security filtering
-   **Message-Driven Architecture** (RabbitMQ)
    -   Order-Service publishes events
    -   Catalog-Service consumes
-   **PostgreSQL**
-   **Docker Compose orchestration**
-   **Multi-stage Docker builds**
-   **Environment-specific configuration**
    -   `application.yml`, `application-dev.yml`, `application-prod.yml`
    -   `.env` file per service
-   **Global Exception Handling** with `@RestControllerAdvice` + `@Slf4j`
-   **Swagger documentation**

------------------------------------------------------------------------

## 🧱 Architecture Overview

Client → Gateway → Eureka → (Auth, Catalog, Order)  

RabbitMQ Events → Catalog-Service (Stock Updates)


| Service            | Port | Responsibilities |
|--------------------|------|------------------|
| **gateway-service** | 8080 | Entry point, route requests, validate JWT |
| **auth-service**    | 8081 | Register, login, logout, JWT generation |
| **catalog-service** | 8082 | CRUD for books, react to order events (RabbitMQ consumer) |
| **order-service**   | 8083 | Create and manage orders, publish RabbitMQ events |
| **eureka-service**  | 8761 | Service discovery dashboard |

------------------------------------------------------------------------

## 🐳 Running with Docker

### Steps

``` bash
git clone https://github.com/OzneKx/bookstore-microservices
cd bookstore-microservices
docker compose up --build
```

### Access points

-   **Gateway** → http://localhost:8080
-   **Eureka Dashboard** → http://localhost:8761
-   **Swagger UI**:
    -   Auth: http://localhost:8081/swagger-ui.html
    -   Catalog: http://localhost:8082/swagger-ui.html
    -   Order: http://localhost:8083/swagger-ui.html
-   **RabbitMQ Dashboard** → http://localhost:15672
    -   user: `guest`
    -   pass: `guest`

------------------------------------------------------------------------

## 🔐 Authentication Flow (JWT)

1.  User logs in through `auth-service`

2.  A JWT is returned

3.  Client sends requests through the gateway:

        Authorization: Bearer <token>

4.  Gateway-Service validates the token using its own `JwtUtil`

5.  If valid, the request is forwarded to the internal services

6.  Internal services trust the gateway and do not re-validate

------------------------------------------------------------------------

## 🔁 Message-Driven Communication (RabbitMQ)

### Flow Example

order-service → publishes "order.created"  
↓  
RabbitMQ Exchange  
↓  
catalog-service consumes the event → updates stock

------------------------------------------------------------------------

## 📌 REST Endpoints

Below are the main routes exposed by each microservice.


### 🔒 Auth Service --- `/auth`

### **POST /auth/register**

Register a new user.\
**201 Created**

Request:

``` json
{
  "name": "Kenzo",
  "email": "kenzo@example.com",
  "password": "123456"
}
```

------------------------------------------------------------------------

### **POST /auth/login**

Authenticate and obtain a JWT.\
**200 OK**

Request:

``` json
{
  "email": "kenzo@example.com",
  "password": "123456"
}
```

Response:

``` json
{ "token": "eyJhbGciOi..." }
```

------------------------------------------------------------------------

### **POST /auth/logout**

Invalidate the current JWT.\
Requires:

    Authorization: Bearer <token>

**204 No Content**

------------------------------------------------------------------------

## 📚 Catalog Service --- `/books`

### **POST /books**

Create a new book.\
**201 Created**

Request:

``` json
{
  "title": "Foundation",
  "author": "Isaac Asimov",
  "isbn": "1234567890",
  "price": 29.90
}
```

------------------------------------------------------------------------

### **GET /books**

Get all books.\
**200 OK**

------------------------------------------------------------------------

### **GET /books/{id}**

Get a book by ID.\
**200 OK** \| **404 Not Found**

------------------------------------------------------------------------

### **PUT /books/{id}**

Update a book.\
**200 OK**

------------------------------------------------------------------------

### **DELETE /books/{id}**

Delete a book.\
**204 No Content**

------------------------------------------------------------------------

## 📦 Order Service --- `/orders`

### **POST /orders**

Create a new order.\
Fetches book prices from catalog-service via HTTP\
Publishes an event to RabbitMQ\
**201 Created**

Request:

``` json
{
  "userId": 1,
  "items": [
    { "bookId": 2, "quantity": 1 },
    { "bookId": 5, "quantity": 3 }
  ]
}
```

------------------------------------------------------------------------

### **GET /orders**

List all orders.\
**200 OK**

------------------------------------------------------------------------

### **GET /orders/{id}**

Get order details.\
**200 OK** \| **404 Not Found**

------------------------------------------------------------------------

### **PATCH /orders/{id}/cancel**

Cancel an order.\
**204 No Content**

------------------------------------------------------------------------

## 🧪 Validation, Errors & Exception Handling

All services use:

-   `@Valid` for request validation
-   `@RestControllerAdvice` for global error handling
-   Structured error responses with `ApiError`
-   Central logging via Lombok `@Slf4j`

------------------------------------------------------------------------

## 🔧 Technology Stack

-   **Java 21**
-   **Spring Boot 3**
-   **Spring Web**
-   **Spring Security**
-   **Spring Cloud Netflix Eureka**
-   **Spring Cloud Gateway**
-   **RabbitMQ**
-   **PostgreSQL**
-   **Docker / Docker Compose**
-   **Lombok**
-   **Swagger / OpenAPI**
-   **RestTemplate**

------------------------------------------------------------------------

## 🌱 Environment Configuration

Each service uses:

    application.yml
    application-dev.yml
    application-prod.yml

Each service uses its own .env file:

    .env.auth
    .env.catalog
    .env.order
    .env.gateway

------------------------------------------------------------------------

## 📈 Future Improvements

-   Add Spring Cloud Config Server
-   Add distributed tracing (Zipkin / Sleuth)
-   Replace RestTemplate with OpenFeign
-   Add Prometheus + Grafana dashboards
-   Introduce Circuit Breaker (Resilience4j)

------------------------------------------------------------------------

## 🧑‍💻 About the Author

**Kenzo de Albuquerque**\
Software Engineer (PUCPR, 2025)\
Backend Developer --- Java, Spring Boot, Cloud, Microservices
