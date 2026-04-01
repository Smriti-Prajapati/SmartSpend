# SmartSpend — Smart Expense Tracker

A production-ready full-stack expense tracker built with Spring Boot + React.

## Tech Stack
- **Backend**: Java 17, Spring Boot 3.2, Spring Security, JWT, Hibernate/JPA
- **Frontend**: React 18, Chart.js, React Router v6
- **Database**: MySQL 8
- **Build**: Maven, npm
- **Deploy**: Docker + Docker Compose

---

## Quick Start (Local)

### Prerequisites
- Java 17+, Maven 3.9+
- Node.js 18+, npm
- MySQL 8 running on port 3306

### 1. Database
MySQL database `smartspend` is auto-created on first run.
Default credentials: `root / root` — change in `application.properties` if needed.

### 2. Backend
```bash
cd smartspend/backend
mvn spring-boot:run
# Runs on http://localhost:8080
```

### 3. Frontend
```bash
cd smartspend/frontend
npm install
npm start
# Runs on http://localhost:3000
```

---

## Docker (Recommended)

```bash
cd smartspend
docker-compose up --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api

---

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/register | Register new user |
| POST | /api/auth/login | Login, returns JWT |
| GET | /api/dashboard | Dashboard summary |
| GET/POST | /api/expenses | List / create expenses |
| PUT/DELETE | /api/expenses/{id} | Update / delete expense |
| GET/POST | /api/income | List / create income |
| PUT/DELETE | /api/income/{id} | Update / delete income |
| GET | /api/insights | Smart spending insights |
| GET | /api/categories | All categories |
| GET | /api/user/profile | Get profile |
| PUT | /api/user/profile | Update profile |
| GET | /api/export/pdf?startDate=&endDate= | Export PDF report |

All endpoints except `/api/auth/**` and `/api/categories` require `Authorization: Bearer <token>`.

---

## Deployment (Render / Railway)

### Backend
1. Push to GitHub
2. Create a new Web Service → select repo → set build command: `mvn package -DskipTests`
3. Set start command: `java -jar target/smartspend-backend-1.0.0.jar`
4. Add environment variables: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`

### Frontend
1. Create a new Static Site → select repo → root: `frontend`
2. Build command: `npm install && npm run build`
3. Publish dir: `build`
4. Set `REACT_APP_API_URL` if not using proxy

---

## Features
- JWT authentication (register / login / logout)
- Dashboard with balance, income, expense cards
- Monthly bar chart + category pie chart
- Expense management (add/edit/delete + search/filter)
- Income management
- Smart Insights (trends, alerts, AI suggestions)
- Dark mode toggle
- PDF report export
- Budget limit alerts
- Responsive mobile-first design
