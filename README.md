# 🛒 E-Commerce Microservices Platform

A production-ready, cloud-native e-commerce backend built with **Spring Boot 3** and **Spring Cloud**, featuring JWT security, distributed tracing, event-driven messaging, and full containerization.

---

## 📐 Architecture Overview

```
                        ┌─────────────────────────────────────────────────────┐
                        │              CLIENT (Postman / Frontend)             │
                        └──────────────────────┬──────────────────────────────┘
                                               │  HTTPS
                        ┌──────────────────────▼──────────────────────────────┐
                        │              API GATEWAY  (:8080)                   │
                        │   JWT Auth Filter │ Rate Limiter │ Circuit Breaker  │
                        │              Logging Filter                         │
                        └───┬──────┬──────┬───────┬──────┬──────┬────────────┘
                            │      │      │       │      │      │
               ┌────────────▼┐  ┌──▼───┐ │  ┌───▼──┐ ┌─▼───┐ ┌▼──────────┐
               │  Customer   │  │Product│ │  │ Price│ │Cart │ │  Order    │
               │  Service    │  │Service│ │  │Service│ │Svc  │ │  Service  │
               │  (:8081)    │  │(:8082)│ │  │(:8083)│ │(:8084)│ │ (:8085) │
               └─────────────┘  └───────┘ │  └──────┘ └─────┘ └────┬──────┘
                                           │                         │ Kafka
                        ┌──────────────────┘                   ┌────▼──────────┐
                        │  Config Server (:8888)                │ Notification  │
                        │  Eureka Server (:8761)                │ Service(:8086)│
                        └──────────────────────────────────────└───────────────┘
```

---

## 🧩 Microservices

| Service | Port | Description |
|---|---|---|
| **Eureka Server** | 8761 | Service registry & discovery |
| **Config Server** | 8888 | Centralized configuration management |
| **API Gateway** | 8080 | Single entry point, security & routing |
| **Customer Service** | 8081 | User registration, profiles, authentication |
| **Product Service** | 8082 | Product catalog, inventory management |
| **Price Service** | 8083 | Dynamic pricing, discounts |
| **Cart Service** | 8084 | Shopping cart management |
| **Order Service** | 8085 | Order lifecycle management |
| **Notification Service** | 8086 | Kafka consumer, email/event notifications |

---

## ⚙️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.3.11 |
| **Cloud** | Spring Cloud 2023.0.3 |
| **Security** | JWT (JSON Web Tokens) |
| **Messaging** | Apache Kafka |
| **Database** | MySQL 8 |
| **Resilience** | Resilience4j (Circuit Breaker) |
| **Tracing** | Zipkin Distributed Tracing |
| **Containerization** | Docker & Docker Compose |
| **Service Discovery** | Netflix Eureka |
| **Config Management** | Spring Cloud Config |

---

## 🔐 Security

Authentication and authorization are handled at the **API Gateway** level using JWT:

- All requests must include a valid JWT token in the `Authorization: Bearer <token>` header
- The gateway validates the token before routing to downstream services
- Unauthenticated requests are rejected at the gateway — downstream services never see invalid requests
- Token generation happens via the **Customer Service** (`/auth/login`)

```
Request → API Gateway
            ├── JWT Filter (validate token)
            ├── Rate Limiter (throttle abuse)
            ├── Logging Filter (audit trail)
            └── Route to Service
```

---

## 🔄 Event-Driven Architecture

Order placement triggers an asynchronous notification via **Apache Kafka**:

```
Order Service  ──► Kafka Topic: order-events ──► Notification Service
   (Producer)                                        (Consumer)
```

- **Order Service** publishes an `OrderPlacedEvent` when an order is confirmed
- **Notification Service** consumes the event and sends confirmation notifications
- Decoupled design ensures order processing is not blocked by notification delivery

---

## 🛡️ Resilience

Circuit breaker pattern implemented with **Resilience4j** on the API Gateway:

| State | Description |
|---|---|
| **Closed** | Normal operation — requests flow through |
| **Open** | Service is failing — requests are short-circuited with fallback |
| **Half-Open** | Trial mode — limited requests allowed to test recovery |

---

## 📡 Distributed Tracing

All inter-service calls are traced using **Zipkin**. Each request gets a unique `traceId` that can be used to follow the request through every service.

- **Zipkin Dashboard**: `http://localhost:9411`
- Trace headers are automatically propagated via Spring Cloud Sleuth

---

## 🚀 Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 17+
- Maven 3.8+

### Run with Docker Compose

```bash
# Clone the repository
git clone https://github.com/Meghna014/ecommerce-microservices
cd ecommerce-microservices

# Start all infrastructure (MySQL, Kafka, Zipkin)
docker-compose up -d

# Build all services
mvn clean package -DskipTests

# Start services (in order)
# 1. Config Server
# 2. Eureka Server
# 3. API Gateway
# 4. Business Services
```

### Service Startup Order

```
1. Config Server   → 2. Eureka Server   → 3. API Gateway
       ↓
4. Customer  →  5. Product  →  6. Price  →  7. Cart  →  8. Order  →  9. Notification
```

---

## 📡 API Reference

### Authentication

```http
POST /api/customers/auth/register
POST /api/customers/auth/login
```

### Products

```http
GET    /api/products
GET    /api/products/{id}
POST   /api/products          # Admin only
PUT    /api/products/{id}     # Admin only
DELETE /api/products/{id}     # Admin only
```

### Cart

```http
GET    /api/cart/{customerId}
POST   /api/cart/add
DELETE /api/cart/remove/{itemId}
```

### Orders

```http
POST   /api/orders/place
GET    /api/orders/{customerId}
GET    /api/orders/detail/{orderId}
```

> All endpoints (except `/auth/**`) require `Authorization: Bearer <jwt_token>` header.

---

## 🐳 Docker Infrastructure

```yaml
# Key services in docker-compose.yml
services:
  mysql:       # Port 3306 — Persistent data store
  kafka:       # Port 9092 — Event streaming
  zipkin:      # Port 9411 — Distributed tracing UI
```

---

## 📊 Monitoring & Observability

| Tool | URL | Purpose |
|---|---|---|
| **Eureka Dashboard** | http://localhost:8761 | Service registry — all registered instances |
| **Zipkin UI** | http://localhost:9411 | Distributed trace visualization |
| **Actuator Health** | http://localhost:{port}/actuator/health | Per-service health check |

---

## 🧪 Testing

API tests are available as a **Postman Collection** in `postman/Microservices Ecommerce.postman_collection.json`.

Import the collection and set the following environment variables:

| Variable | Value |
|---|---|
| `base_url` | `http://localhost:8080` |
| `token` | *(populated automatically after login)* |

---

## 📁 Project Structure

```
ecommerce-microservices/
├── config-server/
├── eureka-server/
├── api-gateway/
│   ├── filters/
│   │   ├── JwtAuthFilter.java
│   │   ├── LoggingFilter.java
│   │   └── RateLimitingFilter.java
├── customer-service/
├── product-service/
├── price-service/
├── cart-service/
├── order-service/
├── notification-service/
├── docker-compose.yml
└── README.md
