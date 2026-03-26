# 🚆 IRCTC-Style Ticket Booking System (High Concurrency System Design)

## 📌 Goal

Build a **highly scalable ticket booking system** capable of handling:

* 100K concurrent users
* Limited seat inventory (e.g., 1000 seats)
* No duplicate bookings
* High availability and fault tolerance

---

# 🏗️ System Architecture Overview

We will build this system incrementally using **microservices architecture**.

## 🔹 Final Target Architecture (Microservices)

1. **API Gateway Service**
2. **User Service**
3. **Train & Inventory Service**
4. **Booking Service**
5. **Seat Allocation Service (Critical)**
6. **Payment Service**
7. **Notification Service**
8. **Queue System (Kafka)**
9. **Redis (Cache + Locking)**
10. **PostgreSQL (Primary DB)**

---

# 🧱 Phase-Based Development Plan

We will NOT build everything at once.

---

## ✅ Phase 1: Monolith (Foundation)

### Goal:

Build a working system with:

* Basic booking flow
* DB schema
* REST APIs

### Services:

* Single Spring Boot application

### Features:

* Create train
* View seat availability
* Book ticket
* Store booking

### Tech:

* Java + Spring Boot
* PostgreSQL (Docker)
* Docker Compose

---

## ✅ Phase 2: Introduce Caching (Performance)

### Add:

* Redis (Docker)

### Features:

* Cache seat availability
* Reduce DB reads

---

## ✅ Phase 3: Prevent Double Booking

### Add:

* Redis Distributed Lock

### Features:

* Lock per train/date
* Ensure only one booking at a time

---

## ✅ Phase 4: Introduce Queue (Kafka)

### Why:

Handle traffic spikes (Tatkal scenario)

### Add:

* Kafka (Docker)

### Flow Change:

* Booking request → Kafka
* Consumer processes booking

---

## ✅ Phase 5: Split into Microservices

Break monolith into:

| Service                 | Responsibility          |
| ----------------------- | ----------------------- |
| API Gateway             | Routing + Rate limiting |
| Booking Service         | Accept requests         |
| Seat Allocation Service | Assign seats            |
| Inventory Service       | Manage availability     |
| Payment Service         | Handle transactions     |

---

## ✅ Phase 6: High Concurrency Optimization

* Partition Kafka topics (by trainId)
* DB partitioning (by date/train)
* Horizontal scaling

---

## ✅ Phase 7: Production Readiness

* Logging (ELK)
* Metrics (Prometheus + Grafana)
* Rate limiting
* Circuit breakers

---

# 🧠 Detailed Microservices Breakdown

---

## 1️⃣ API Gateway

### Responsibilities:

* Route requests
* Rate limiting
* Authentication

### Tech:

* Spring Cloud Gateway

---

## 2️⃣ Booking Service

### Responsibilities:

* Accept booking request
* Push to Kafka

### Endpoint:

POST /book

---

## 3️⃣ Seat Allocation Service (🔥 Core Service)

### Responsibilities:

* Consume Kafka events
* Allocate seats safely
* Handle concurrency

---

## 4️⃣ Inventory Service

### Responsibilities:

* Maintain seat availability
* Sync with DB + Redis

---

## 5️⃣ Payment Service

### Responsibilities:

* Simulate payment
* Handle success/failure

---

## 6️⃣ Notification Service

### Responsibilities:

* Send booking confirmation

---

# 🗄️ Database Design

## Tables:

### trains

* id
* name

### seats

* id
* train_id
* seat_number
* class

### bookings

* id
* user_id
* train_id
* seat_number
* status
* created_at

### booking_requests

* id
* request_id (idempotency key)
* status

---

## 🔒 Constraints

* UNIQUE(train_id, seat_number, journey_date)

---

# 🔄 Booking Flow (Final Architecture)

1. User → API Gateway
2. Booking Service → Kafka
3. Seat Allocation Service:

    * Acquire Redis Lock
    * Allocate seat
    * Update DB
4. Payment Service
5. Confirmation

---

# ⚙️ Tech Stack

## Backend

* Java 17+
* Spring Boot

## Database

* PostgreSQL

## Cache

* Redis

## Messaging

* Kafka

## Infra

* Docker
* Docker Compose

## Monitoring (Later)

* Prometheus
* Grafana

---

# 🐳 Docker Strategy

We will NOT install anything locally.

## Services via Docker:

* PostgreSQL
* Redis
* Kafka

---

## Sample docker-compose (Phase 1)

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    container_name: booking-postgres
    environment:
      POSTGRES_DB: booking_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin
    ports:
      - "5432:5432"

  redis:
    image: redis:7
    container_name: booking-redis
    ports:
      - "6379:6379"
```

---

# 🧪 Implementation Plan (Step-by-Step)

---

## 🚀 Step 1: Project Setup

* Create Spring Boot project
* Add dependencies:

    * Spring Web
    * Spring Data JPA
    * PostgreSQL Driver

---

## 🚀 Step 2: DB Schema

* Create tables
* Add constraints

---

## 🚀 Step 3: Basic APIs

* Create Train
* Get Availability
* Book Ticket

---

## 🚀 Step 4: Booking Logic

* Fetch available seats
* Assign seat
* Save booking

---

## 🚀 Step 5: Add Redis

* Cache availability
* Reduce DB calls

---

## 🚀 Step 6: Add Locking

* Redis SETNX lock
* Prevent race conditions

---

## 🚀 Step 7: Introduce Kafka

* Produce booking event
* Consume and process

---

## 🚀 Step 8: Break into Services

* Extract Booking Service
* Extract Seat Allocation Service

---

## 🚀 Step 9: Scale

* Add partitions
* Load testing

---

# 🧠 Key Engineering Concepts You Will Master

* Distributed locking
* Idempotency
* Event-driven architecture
* High concurrency handling
* Backpressure
* Database constraints vs application logic
* System bottlenecks

---

# ⚔️ Rules for This Project

1. Do NOT jump steps
2. Each phase must be runnable
3. Write clean, production-like code
4. Think failure scenarios always
5. Optimize only after correctness

---

# 🚀 Next Step

👉 Start **Phase 1: Monolith**

Once done, we will:

* Review design
* Identify bottlenecks
* Move to next phase

---

🔥 This project, if done properly, is enough to crack **any Tech Lead interview**.
