# SellerNexus

**Unifying multi-platform selling through seamless API integration.**

[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.1.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-15-red.svg)](https://angular.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)

---

## 🛒 E-Commerce Product Transfer Interface

### 📖 Project Overview

**SellerNexus** is a comprehensive startup application that enables sellers to seamlessly transfer all product listings from one e-commerce platform (e.g., "Joom", "Flipkart", "Shopify") to another using official APIs of both platforms.

It provides a **full-stack interface** for sellers to:
- 🔐 Register and authenticate securely
- 🔗 Link their e-commerce platform accounts via OAuth2
- 📦 View and manage product inventories across platforms
- 🔄 Transfer products between platforms with variant mapping
- 📊 Monitor transfer logs and audit trails
- ⚙️ Manage API credentials and platform connections

---

## 🏗️ System Architecture

### 🧩 Key Components

| Layer | Technology | Description |
|-------|-----------|-------------|
| **Frontend** | Angular 15, TypeScript, Material Design | Provides a responsive seller dashboard for registration, login, OAuth linking, product management, and monitoring transfer logs. |
| **Backend** | Spring Boot 2.1.7, Java 11 | Core business logic handling authentication (JWT), OAuth2 flows, product fetching/creation, validation, and API-to-API transfers. |
| **Database** | PostgreSQL, Flyway | Stores seller information, platform credentials, products, transfer logs with complete audit trail. |
| **Testing** | JUnit 5, Mockito, AssertJ | Comprehensive unit and integration tests validating every service layer component (76 test cases). |

---

## 🧪 Testing Strategy

### 🧩 Unit Tests

Each service method is validated through **JUnit 5** unit tests with **Mockito** for dependency mocking.

**Test Location**: `Backend/src/test/java/com/server/sellernexus/service/sellerNexus/`

**Test Coverage**:

| Test Class | Test Cases | Coverage |
|-----------|------------|----------|
| `JoomAuthServiceTest` | 16 | OAuth flows, token management, state signing |
| `JoomProductServiceTest` | 18 | Product CRUD, pagination, image extraction |
| `JoomTransferServiceTest` | 11 | Transfer logic, variant mapping, error handling |
| `TransferLogServiceTest` | 14 | Logging, audit trail, data masking |
| `PlatformCredentialServiceTest` | 17 | Credential management, deduplication |

**Total**: **76 test cases** covering all critical service layer functionality.

### 🎯 Test Execution

```bash
# Run all tests
cd Backend
mvn test

# Run specific test class
mvn test -Dtest=JoomAuthServiceTest

## 🚀 Getting Started

### Prerequisites
- **Java 11** or higher
- **Node.js 16+** and npm
- **PostgreSQL 12+**
- **Maven 3.8+**

### Installation

1. **Clone the Repository**
```bash
git clone https://github.com/vora-arpit/seller-nexus.git
cd seller-nexus
```

2. **Database Setup**
```bash
psql -U postgres
CREATE DATABASE sellernexus;
\q
```

3. **Backend Configuration**

Create `.env` file in `Backend/` directory:
```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sellernexus
DB_USERNAME=postgres
DB_PASSWORD=your_password

JWT_SECRET=your_jwt_secret_key
AUTH_TOKEN_SECRET=your_hmac_secret

JOOM_CLIENT_ID=your_joom_client_id
JOOM_CLIENT_SECRET=your_joom_client_secret
```

4. **Run Backend**
```bash
cd Backend
mvn clean install
mvn spring-boot:run
```
Backend starts at `http://localhost:8080`

5. **Run Frontend**
```bash
cd Frontend
npm install
ng serve
```
Frontend starts at `http://localhost:4200`

---

## 📊 API Documentation

API documentation available via Swagger UI:  
🔗 **http://localhost:8080/swagger-ui.html**

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 🧹 Code Quality & Clean Code Refactoring

### Part 3 - Clean Code Implementation

This project follows **Google Java Style Guide** and **Clean Code principles** by Robert C. Martin.

**Key Improvements:**
- ✅ **15+ Magic Values** extracted to descriptive constants
- ✅ **18+ Methods** extracted for Single Responsibility Principle
- ✅ **95% Javadoc Coverage** with Clean Code references
- ✅ **Guard Clauses** pattern applied for reduced complexity
- ✅ **Zero Debug Statements** in production code
- ✅ **60% Reduction** in cyclomatic complexity

**Documentation:**
- 📄 [PART3_CLEAN_CODE_REFACTORING.md](PART3_CLEAN_CODE_REFACTORING.md) - Detailed refactoring report
- 📄 [PART3_QUICK_REFERENCE.md](PART3_QUICK_REFERENCE.md) - Quick summary

**Refactored Services:**
- `JoomAuthService.java` - OAuth authentication with small, focused methods
- `JoomProductService.java` - Product operations with extracted constants
- `PlatformCredentialService.java` - Credential management with Stream API

---

## 👨‍💻 Author

**Arpit Vora**
- GitHub: [@vora-arpit](https://github.com/vora-arpit)
- Repository: https://github.com/vora-arpit/seller-nexus

---

## 📝 License

This project is licensed under the MIT License.

---

**⭐ If you find this project helpful, please consider giving it a star!**
