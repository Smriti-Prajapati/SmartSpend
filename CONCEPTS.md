# Advanced Java Concepts Used in SmartSpend

---

## 1. Spring Boot Framework
Spring Boot is used as the core backend framework to simplify application development. It provides auto-configuration, embedded servers, and production-ready features.

Key Usage:
- Rapid backend development
- REST API creation
- Dependency management

---

## 2. RESTful API Development
The application follows REST architecture for communication between frontend and backend.

HTTP Methods Used:
- GET → Retrieve data
- POST → Create new records
- PUT → Update existing records
- DELETE → Remove records

---

## 3. MVC (Model-View-Controller) Architecture
The project is structured using MVC design pattern.

Components:
- Model → Represents data and database entities
- View → Frontend UI (HTML, CSS, JS)
- Controller → Handles requests and responses

Benefit:
- Clean code structure
- Easy maintenance and scalability

---

## 4. Java Persistence API (JPA) & Hibernate
Used for Object Relational Mapping (ORM) to interact with the database.

Features:
- Maps Java objects to database tables
- Reduces boilerplate SQL code
- Supports CRUD operations

---

## 5. Database Connectivity (JDBC Concepts)
Underlying database operations are based on JDBC principles for connecting Java applications with MySQL.

---

## 6. MySQL Database Integration
Used as the primary database to store application data.

Tables Include:
- Users
- Expenses
- Income
- Categories

---

## 7. Dependency Injection (DI)
Spring Boot uses Dependency Injection to manage object creation and dependencies.

Advantages:
- Loose coupling
- Better testability
- Improved modularity

---

## 8. Authentication & Security (JWT + BCrypt)
Security is implemented using modern techniques.

Features:
- Password hashing using BCrypt
- JWT (JSON Web Token) for authentication
- Secure login and session handling

---

## 9. Exception Handling
Custom and global exception handling is implemented to ensure smooth application performance.

Benefits:
- Prevents crashes
- Provides meaningful error messages

---

## 10. Validation (Input Handling)
Input validation is used to ensure correct and secure data entry.

Examples:
- Required fields validation
- Data type checks
- Preventing invalid inputs

---

## 11. Build Tool (Maven)
Maven is used for project build and dependency management.

Functions:
- Handles external libraries
- Automates build process

---

## 12. Multithreading (Conceptual)
The application handles multiple user requests simultaneously, based on multithreading concepts used in web servers.

---

## 13. API Integration
Frontend and backend are connected using APIs.

Purpose:
- Data exchange between UI and server
- Real-time updates

---

## 14. Layered Architecture
The application follows a layered structure:
- Controller Layer → Handles HTTP requests
- Service Layer → Business logic
- Repository Layer → Database operations

---

## 15. Session Management
User sessions are managed securely using JWT tokens instead of traditional session tracking.

---

## 16. CRUD Operations
Core operations implemented:
- Create → Add expenses/income
- Read → View transactions
- Update → Modify entries
- Delete → Remove records

---

## 17. Data Serialization (JSON)
Data is transferred between frontend and backend in JSON format.
