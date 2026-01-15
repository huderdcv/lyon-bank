# 🏦 LyonBank Core: High-Performance Banking Engine

[![Java](https://img.shields.io/badge/Java-21_LTS-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4+-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Security-OAuth2_RSA-red)](https://spring.io/projects/spring-security)
[![Architecture](https://img.shields.io/badge/Architecture-Modular_Monolith-blue)](https://martinfowler.com/bliki/MonolithFirst.html)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)

> **Production-ready backend simulating a core banking system with strict ACID compliance.**

## 📖 About The Project

**LyonBank Core** is a robust financial transaction engine designed to solve the hardest problems in fintech: **concurrency** and **security**.

This project handles high-concurrency money transfers using **Pessimistic Locking** (Database-level locks) to prevent "Double-Spending" attacks. It is architected as a **Modular Monolith** using **Domain-Driven Design (DDD)** principles, making it ready for microservices migration.

## ⚙️ Key Technical Features

### 1. ⚡ High-Concurrency Transaction Engine

- **Problem:** Race conditions during simultaneous transfers (e.g., Alice sends money to Bob and Charlie at the exact same millisecond).
- **Solution:** Implemented `Pessimistic Locking` (`SELECT ... FOR UPDATE`) in PostgreSQL via Spring Data JPA.
- **Result:** Guarantees atomic balance updates and strict ACID compliance under heavy load.

### 2. 🔐 Bank-Grade Security (OAuth2 + RSA)

- **Stateless Auth:** Powered by **Spring Security 6** Resource Server.
- **Asymmetric Signing:** JWTs are signed with a private RSA key and verified with a public key. This prevents secret-key leakage vulnerabilities common in symmetric (HS256) setups.
- **RBAC:** Granular permissions for `CLIENT`, `ADMIN`, and `AUDITOR`.

### 3. 🧩 Modular Architecture (DDD)

- **Feature-Based Packaging:** Code is organized by domain (`features.accounts`, `features.transactions`) rather than technical layers.
- **Benefit:** Enforces separation of concerns and simplifies future extraction into Microservices.

## 🛠️ Tech Stack

- **Language:** Java 21 (LTS) - Records, Pattern Matching
- **Framework:** Spring Boot 3.4+
- **Database:** PostgreSQL 16
- **Security:** OAuth2 / JWT / RSA Encryption
- **Build:** Maven & Docker Compose

## 🚀 Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 21 SDK

### Installation

1.  **Clone the repository**

    ```bash
    git clone https://github.com/huderdcv/lyonbank-core.git
    ```

2.  **Start the Database**

    ```bash
    docker-compose up -d
    ```

3.  **Generate RSA Keys**

    - Place your `private.pem` and `public.pem` in `src/main/resources/certs`.

4.  **Run the Application**
    ```bash
    ./mvnw spring-boot:run
    ```

## 🔌 API Endpoints

| Method | Endpoint                     | Description                                  |
| :----- | :--------------------------- | :------------------------------------------- |
| `POST` | `/api/auth/register`         | Create a new user (Default role: CLIENT)     |
| `POST` | `/api/auth/login`            | Returns a signed RSA-JWT (Bearer Token)      |
| `POST` | `/api/accounts`              | Open a new Bank Account (Auto-generated CCI) |
| `GET`  | `/api/accounts`              | Get all the accounts of the user             |
| `POST` | `/api/transactions/transfer` | **Atomic money transfer** (Thread-safe)      |

## 👥 Contact

Huder De La Cruz Vasquez
