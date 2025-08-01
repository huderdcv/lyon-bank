# LyonBank Core - High-Performance Banking Engine

A production-ready **Modular Monolith** designed to handle high-concurrency financial transactions securely. This project simulates a core banking backend with strict compliance to ACID properties and Security standards.

## Key Features

### 1. High-Concurrency Transaction Engine

- **Problem:** Double-spending attacks and race conditions during simultaneous transfers.
- **Solution:** Implemented **Pessimistic Locking** (`SELECT ... FOR UPDATE`) in PostgreSQL via JPA. This ensures atomic balance updates even under heavy load.
- **Tech:** Java 21, Spring Data JPA, PostgreSQL.

### 2. Bank-Grade Security (OAuth2 + RSA)

- **Stateless Auth:** implemented using **Spring Security 6** resource server.
- **Asymmetric Signing:** JWTs are signed with a private RSA key and verified with a public key, preventing secret-key leakage vulnerabilities.
- **RBAC (Role-Based Access Control):** Granular permissions for `CLIENT`, `ADMIN`, and `AUDITOR`.

### 3. Domain-Driven Design (Feature-Based)

- Unlike traditional layered architectures (Controller/Service/Dao), this project uses a **Feature-Based Packaging** strategy (`features.accounts`, `features.transactions`).
- **Benefit:** High modularity. Each feature is self-contained, making it easier to refactor into Microservices in the future.

## Tech Stack

- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.4+
- **Database:** PostgreSQL 16
- **Security:** OAuth2 / RSA
- **Build Tool:** Maven
- **Development:** Docker

## How to Run

1.  **Start Database:**
    ```bash
    docker-compose up -d
    ```
2.  **Generate RSA Keys (One-time setup):**
    The application expects `private.pem` and `public.pem` in `src/main/resources/certs`.
3.  **Run Application:**
    ```bash
    ./mvnw spring-boot:run
    ```

## API Endpoints (Postman)

| Method | Endpoint                     | Description                                  |
| :----- | :--------------------------- | :------------------------------------------- |
| `POST` | `/api/auth/register`         | Create a new user (Default role: CLIENT)     |
| `POST` | `/api/auth/login`            | Returns a signed JWT (Bearer Token)          |
| `POST` | `/api/accounts`              | Open a new Bank Account (Auto-generated CCI) |
| `POST` | `/api/transactions/transfer` | Atomic money transfer between accounts       |

---

_Built by Huder DC._
