# Java Concepts Used in SmartSpend

**Spring Boot** — Framework that makes it easy to build Java web applications with minimal setup.

**Dependency Injection** — Spring automatically creates and provides objects wherever they are needed, so you don't create them manually.

**REST API** — A way for the frontend and backend to communicate using HTTP requests like GET, POST, PUT, DELETE.

**JPA / Hibernate** — Automatically maps Java classes to database tables so you don't have to write SQL for basic operations.

**Spring Data JPA** — Generates database queries automatically just by writing method names in the repository interface.

**Spring Security** — Handles login, logout, and controls who can access which parts of the application.

**JWT (JSON Web Token)** — A token given to the user after login. The user sends this token with every request to prove their identity.

**BCrypt** — A method to securely hash passwords before storing them in the database so they can't be read if the database is leaked.

**CORS** — A setting that allows the React frontend to communicate with the Spring backend since they run on different ports.

**DTO (Data Transfer Object)** — A simple class used to send only the required data between the frontend and backend.

**Bean Validation** — Annotations like @NotBlank and @Email that automatically check if the data sent by the user is valid.

**Streams API** — A Java feature to process lists of data in a clean and readable way, like filtering, mapping, and sorting.

**Optional** — A Java feature to safely handle cases where a value might be missing, avoiding null errors.

**CommandLineRunner** — Runs a piece of code automatically when the application starts, used here to add default categories to the database.

**Environment Variables** — Configuration values like database password and JWT secret stored outside the code so they stay secure in production.

**HikariCP** — A connection pool that reuses database connections instead of creating a new one for every request, making the app faster.

**JPQL** — A query language similar to SQL but written using Java class names instead of database table names.

**ResponseEntity** — Used to send HTTP responses with a specific status code like 200 OK or 400 Bad Request from the controller.

**Cascade** — A JPA feature where deleting a parent record automatically deletes all related child records.

**@PreUpdate** — A method that runs automatically before saving an updated record to the database, used to set the updated timestamp.
