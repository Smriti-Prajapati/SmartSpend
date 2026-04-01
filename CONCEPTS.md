# Advanced Java Programming Concepts Used in SmartSpend

## 1. Spring Boot Auto-Configuration
Spring Boot eliminates boilerplate configuration by auto-configuring beans based on classpath dependencies.
In SmartSpend, simply adding `spring-boot-starter-web` auto-configures an embedded Tomcat server, Jackson for JSON, and DispatcherServlet.

```java
@SpringBootApplication  // Combines @Configuration + @EnableAutoConfiguration + @ComponentScan
public class SmartSpendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartSpendApplication.class, args);
    }
}
```

---

## 2. Dependency Injection (DI) and Inversion of Control (IoC)
Spring's IoC container manages object creation and lifecycle. Instead of creating objects manually, Spring injects them.

```java
@Service
public class ExpenseService {
    @Autowired  // Spring injects this automatically
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;
}
```

Types used in SmartSpend:
- `@Autowired` — field injection
- Constructor injection via `@Component`, `@Service`, `@Repository`

---

## 3. Spring MVC — REST API with Annotations
Spring MVC maps HTTP requests to Java methods using annotations.

```java
@RestController                    // Combines @Controller + @ResponseBody
@RequestMapping("/api/expenses")   // Base URL mapping
public class ExpenseController {

    @GetMapping                    // HTTP GET
    @PostMapping                   // HTTP POST
    @PutMapping("/{id}")           // HTTP PUT with path variable
    @DeleteMapping("/{id}")        // HTTP DELETE

    public ResponseEntity<?> create(@Valid @RequestBody ExpenseDTO dto) {
        // @RequestBody deserializes JSON to Java object
        // @Valid triggers Bean Validation
        // ResponseEntity wraps response with HTTP status
    }
}
```

---

## 4. JPA / Hibernate ORM — Object Relational Mapping
Hibernate maps Java classes to database tables without writing SQL.

```java
@Entity                        // Marks class as a database table
@Table(name = "expenses")      // Custom table name
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment PK
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)   // Many expenses → one category
    @JoinColumn(name = "category_id")     // Foreign key column
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)    // Lazy loading — loads only when accessed
    @JoinColumn(name = "user_id")
    private User user;

    @PreUpdate                            // Lifecycle callback
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

---

## 5. Spring Data JPA — Repository Pattern
Spring Data JPA auto-generates SQL from method names — no SQL writing needed.

```java
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Spring generates: SELECT * FROM expenses WHERE user_id=? ORDER BY date DESC
    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    // Custom JPQL query
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    Double sumAmountByUserId(@Param("userId") Long userId);
}
```

---

## 6. Spring Security — Authentication and Authorization

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(c -> c.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

---

## 7. JWT (JSON Web Token) Authentication
Stateless authentication — server issues a signed token, client sends it with every request.

```java
public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
}
```

---

## 8. BCrypt Password Hashing
One-way hashing with built-in salt. Passwords are never stored in plain text.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Registration
user.setPassword(passwordEncoder.encode(request.getPassword()));
```

---

## 9. OncePerRequestFilter — Custom Security Filter
Runs once per HTTP request to validate JWT tokens before reaching controllers.

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String jwt = header.substring(7);
            // validate and set SecurityContext
        }
        chain.doFilter(request, response);
    }
}
```

---

## 10. Bean Validation (JSR-380)

```java
public class RegisterRequest {
    @NotBlank
    private String name;

    @Email @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

---

## 11. JPQL — Java Persistence Query Language
Object-oriented queries on entity classes, not database tables.

```java
@Query("SELECT e.category.name, SUM(e.amount) FROM Expense e " +
       "WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end " +
       "GROUP BY e.category.name")
List<Object[]> getCategoryBreakdown(@Param("userId") Long userId,
                                    @Param("start") LocalDate start,
                                    @Param("end") LocalDate end);
```

---

## 12. Java 8+ Features

### Streams API
```java
return expenseRepository.findByUserIdOrderByDateDesc(userId)
        .stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
```

### Lambda Expressions
```java
transactions.sort((a, b) -> b.getDate().compareTo(a.getDate()));
```

### Optional
```java
User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
```

### Method References
```java
double total = incomes.stream().mapToDouble(IncomeDTO::getAmount).sum();
```

---

## 13. CommandLineRunner — Startup Hook
Runs code after Spring context initializes — used for seeding default categories.

```java
@Component
public class DataInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) return;
        categoryRepository.saveAll(defaultCategories);
    }
}
```

---

## 14. CORS Configuration
Allows React frontend (port 3000) to call Spring backend (port 8081).

```java
config.setAllowedOrigins(List.of("http://localhost:3000"));
config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
config.setAllowCredentials(true);
```

---

## 15. DTO Pattern (Data Transfer Object)
Decouples API layer from database layer.

```java
// Entity has full relationships
public class Expense {
    private User user;
    private Category category;
}

// DTO exposes only what API needs
public class ExpenseDTO {
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
}
```

---

## 16. ResponseEntity — HTTP Response Control

```java
return ResponseEntity.ok(data);                    // 200
return ResponseEntity.badRequest().body(error);    // 400
return ResponseEntity.status(401).body(error);     // 401
return ResponseEntity.notFound().build();          // 404
```

---

## 17. Cascade Operations in JPA

```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<Expense> expenses;
// Deleting a User automatically deletes all their Expenses
```

---

## 18. Environment-based Configuration

```properties
server.port=${PORT:8081}
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/smartspend}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:root}
```

---

## 19. PDF Generation with iTextPDF

```java
Document doc = new Document(PageSize.A4);
PdfWriter.getInstance(doc, outputStream);
doc.open();
doc.add(new Paragraph("SmartSpend Report", titleFont));
PdfPTable table = new PdfPTable(4);
doc.close();
return outputStream.toByteArray();
```

---

## 20. HikariCP Connection Pooling
Spring Boot's default JDBC connection pool — manages reusable DB connections.

```properties
spring.datasource.hikari.maximum-pool-size=1
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.connection-timeout=30000
```
