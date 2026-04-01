# Advanced Java Concepts Used in SmartSpend

## 1. Spring Boot Auto-Configuration
Spring Boot automatically configures the application based on the dependencies added in pom.xml. No manual XML configuration is needed. For example, adding the web dependency auto-sets up an embedded Tomcat server.

## 2. Dependency Injection (DI) and Inversion of Control (IoC)
Instead of creating objects manually, Spring creates and manages them. Objects are "injected" where needed using @Autowired. This makes code loosely coupled and easier to test.

## 3. @RestController and @RequestMapping
@RestController marks a class as a REST API handler. @RequestMapping defines the base URL. Methods inside handle specific HTTP requests like GET, POST, PUT, DELETE.

## 4. JPA / Hibernate ORM
JPA (Java Persistence API) maps Java classes to database tables. Hibernate is the implementation. Using @Entity on a class tells Hibernate to create a corresponding table. No SQL is needed for basic operations.

## 5. @ManyToOne and @OneToMany (Entity Relationships)
These annotations define relationships between tables. @ManyToOne means many expenses belong to one user. @OneToMany means one user has many expenses. JPA handles the foreign keys automatically.

## 6. FetchType.LAZY and FetchType.EAGER
LAZY loading means related data is loaded from the database only when accessed. EAGER loading fetches it immediately. LAZY is preferred for performance.

## 7. Spring Data JPA Repository
By extending JpaRepository, Spring auto-generates common database operations like save, findById, delete. Custom queries can be written just by naming methods correctly — no SQL needed.

## 8. JPQL (Java Persistence Query Language)
JPQL is like SQL but works on Java entity classes instead of database tables. Used with @Query annotation to write custom queries using class and field names.

## 9. Spring Security
A framework that handles authentication and authorization. It intercepts every HTTP request and checks if the user is allowed to access that resource. Configured using SecurityFilterChain.

## 10. JWT (JSON Web Token)
A compact, self-contained token used for authentication. After login, the server generates a JWT and sends it to the client. The client sends this token with every request. The server validates it without storing session data.

## 11. BCrypt Password Hashing
BCrypt is a one-way hashing algorithm used to securely store passwords. The original password can never be recovered from the hash. BCrypt also adds a random salt to prevent rainbow table attacks.

## 12. OncePerRequestFilter
A Spring filter that runs exactly once per HTTP request. Used to intercept requests, extract the JWT token from the Authorization header, validate it, and set the authenticated user in the security context.

## 13. SecurityContextHolder
A Spring Security class that stores the currently authenticated user's details. Once JWT is validated, the user's authentication is stored here so other parts of the app can access it.

## 14. Bean Validation (JSR-380)
Validation annotations like @NotBlank, @Email, @Size placed on DTO fields. When @Valid is used in a controller method, Spring automatically validates the incoming request data before processing it.

## 15. ResponseEntity
A Spring class used to build HTTP responses with a specific status code, headers, and body. Allows returning 200 OK, 400 Bad Request, 401 Unauthorized etc. from controller methods.

## 16. DTO (Data Transfer Object)
A simple class used to transfer data between the API and the client. DTOs hide internal entity structure and expose only the fields needed. Prevents over-exposing database details.

## 17. CommandLineRunner
A Spring Boot interface with a run() method that executes automatically after the application starts. Used in SmartSpend to seed default expense categories into the database on first run.

## 18. CascadeType.ALL
A JPA setting that propagates database operations from parent to child. For example, if a User is deleted, all their Expenses and Incomes are automatically deleted too.

## 19. CORS (Cross-Origin Resource Sharing)
A browser security feature that blocks requests from a different domain. In SmartSpend, CORS is configured to allow the React frontend (localhost:3000) to communicate with the Spring backend (localhost:8081).

## 20. HikariCP Connection Pooling
HikariCP is Spring Boot's default database connection pool. Instead of opening a new database connection for every request, it maintains a pool of reusable connections, improving performance.

## 21. @PreUpdate Lifecycle Callback
A JPA annotation on a method that runs automatically before an entity is updated in the database. Used in SmartSpend to automatically set the updatedAt timestamp whenever an expense or income is modified.

## 22. Environment Variables in Spring Boot
Spring Boot can read configuration values from environment variables at runtime using ${VARIABLE_NAME:defaultValue} syntax. This allows different settings for local development and production deployment without changing code.

## 23. Streams API (Java 8)
A functional-style API for processing collections. Used in SmartSpend to convert lists of database entities into DTOs, filter records, calculate totals, and sort transactions — all in a clean, readable way.

## 24. Optional (Java 8)
A container that may or may not hold a value. Used to safely handle cases where a database query might return no result, avoiding NullPointerException. Methods like orElseThrow() provide clean error handling.

## 25. Method References (Java 8)
A shorthand for lambda expressions that call an existing method. Written as ClassName::methodName. Used in SmartSpend for mapping entities to DTOs and calculating sums from lists.
