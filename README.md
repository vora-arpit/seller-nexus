# seller-nexus
Unifying multi-platform selling through seamless API integration.

# 🛒 E-Commerce Product Transfer Interface

## 📖 Project Overview
This startup application enables a **seller** to seamlessly **transfer all product listings** from one e-commerce platform (e.g., “Source Platform”) to another (e.g., “Destination Platform”) using official APIs of both platforms.  
It provides a full-stack interface for sellers to register, log in, link their e-commerce accounts, and initiate transfers securely.

---

## 🧱 System Architecture

### 🧩 Key Components
| Layer | Description |
|--------|--------------|
| **Frontend (Angular)** | Provides a responsive seller dashboard for registration, login, API key linking, and monitoring transfer logs. |
| **Backend (Spring Boot + Java)** | Core business logic handling authentication, product fetching, validation, and API-to-API transfers. |
| **Database (MySQL / PostgreSQL)** | Stores seller info, credentials, products, and transfer logs. |
| **Testing (JUnit + Mockito)** | Includes both **unit** and **integration** tests for validating every part of the backend. |

---

## 🧪 Testing Strategy

### 🧩 Unit Tests
Each service method is validated through **JUnit 5** unit tests located under: