# Habit Tracker API 🎯

A RESTful API for managing daily, weekly, and monthly habits with JWT authentication and role-based access control.

---

## 🚀 Technologies

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Security** with JWT
- **Spring Data JPA**
- **MySQL 8.0**
- **Swagger/OpenAPI** for API documentation
- **Lombok** for cleaner code
- **SLF4J** for logging
- **Spring Cache** for performance optimization
- **Docker** & Docker Compose
- **Maven** for dependency management

---

## 📋 Features

### Authentication & Authorization

- User registration and login
- JWT token-based authentication (24-hour expiration)
- Role-based access control (USER and ADMIN roles)

### User Features (ROLE_USER)

- Create, read, update, and delete personal habits
- Complete habits and track streaks (current and longest)
- View personal statistics (completion rate, total habits, best streak)
- Access rankings filtered by frequency (DAILY/WEEKLY/MONTHLY)

### Admin Features (ROLE_ADMIN)

- View all users in the system
- View all habits from all users
- Delete users (cascades to their habits)
- Access global statistics

### Habit System

- **Frequencies:** DAILY, WEEKLY, MONTHLY
- **Target Count:** Set goals like "3 times per week"
- **Automatic Streak Calculation:**
  - DAILY: Consecutive days
  - WEEKLY: Consecutive weeks
  - MONTHLY: Consecutive months
- **Rankings:** Top 10 habits per frequency category

---

## 🗂️ Project Structure
```
habit-tracker/
├── src/main/java/com/habittracker/habit_tracker/
│   ├── controller/          # REST endpoints
│   ├── service/             # Business logic
│   ├── repository/          # Data access layer
│   ├── model/               # JPA entities
│   ├── dto/                 # Data transfer objects
│   │   ├── request/         # Request DTOs
│   │   ├── response/        # Response DTOs
│   │   └── mapper/          # Entity-DTO mappers
│   ├── security/            # JWT & Spring Security config
│   ├── config/              # Application configuration
│   └── exceptions/          # Custom exceptions
├── src/main/resources/
│   ├── application.properties
│   └── logback-spring.xml
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## 🛠️ Installation & Setup

### Prerequisites

- **Docker Desktop** (recommended) OR
- **Java 17** or higher
- **Maven 3.9+**
- **MySQL 8.0**

### Steps

#### Option 1: Using Docker (Recommended)
```bash
# Clone the repository
git clone https://github.com/christo256/S5.02-Web-Application-Habit-Tracker.git
cd S5.02-Web-Application-Habit-Tracker

# Start all services (MySQL + Backend)
docker-compose up -d --build

# Check logs
docker logs -f habit_tracker_backend
```

The API will start on **http://localhost:8080**

---

#### Option 2: Local Installation
```bash
# Clone the repository
git clone https://github.com/christo256/S5.02-Web-Application-Habit-Tracker.git
cd S5.02-Web-Application-Habit-Tracker

# Setup MySQL
mysql -u root -p
CREATE DATABASE habit_tracker;
CREATE USER 'habituser'@'localhost' IDENTIFIED BY 'habitpass';
GRANT ALL PRIVILEGES ON habit_tracker.* TO 'habituser'@'localhost';
FLUSH PRIVILEGES;
exit;

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will start on **http://localhost:8080**

---

## 📖 API Documentation

Once the application is running, access the interactive Swagger UI at:

**http://localhost:8080/swagger-ui.html**

### Quick Start with Swagger

1. Open Swagger UI
2. Use `POST /api/auth/login` to authenticate
3. Copy the JWT token from the response
4. Click the **"Authorize"** button (top right)
5. Paste your token: `Bearer YOUR_TOKEN`
6. Now you can test all protected endpoints!

---

## 🔑 Authentication Flow

### Register a new user
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 2,
    "username": "john",
    "role": "ROLE_USER"
  }
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Using the token

Add the token to the Authorization header in all protected requests:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 📚 Main Endpoints

### Authentication (Public)

| Method | Endpoint             | Description                     |
| ------ | -------------------- | ------------------------------- |
| POST   | `/api/auth/register` | Register a new user (ROLE_USER) |
| POST   | `/api/auth/login`    | Login and get JWT token         |

### Habits (Requires Authentication)

| Method | Endpoint                          | Description                      |
| ------ | --------------------------------- | -------------------------------- |
| POST   | `/api/habits`                     | Create a new habit               |
| GET    | `/api/habits`                     | Get all user's habits            |
| GET    | `/api/habits/{id}`                | Get habit by ID                  |
| PUT    | `/api/habits/{id}`                | Update habit                     |
| DELETE | `/api/habits/{id}`                | Delete habit                     |
| POST   | `/api/habits/{id}/complete`       | Complete habit (updates streaks) |
| GET    | `/api/habits/stats`               | Get personal statistics          |
| GET    | `/api/habits/rankings/{frequency}`| Get top 10 by frequency          |

### Admin (Requires ROLE_ADMIN)

| Method | Endpoint                 | Description        |
| ------ | ------------------------ | ------------------ |
| GET    | `/api/admin/users`       | Get all users      |
| GET    | `/api/admin/habits`      | Get all habits     |
| DELETE | `/api/admin/users/{id}`  | Delete a user      |
| GET    | `/api/admin/stats`       | Global statistics  |

---

## 💾 Database

The application uses **MySQL 8.0** for production.

### Database Schema

**Users**
- id, username (unique), password (encrypted), role

**Habits**
- id, name, description, frequency, targetCount, currentStreak, longestStreak, lastCompleted, user_id (FK)

### Accessing MySQL in Docker
```bash
docker exec -it habit_tracker_mysql mysql -u root -prootpassword habit_tracker
```

---

## 🔐 Default Admin User

The application automatically creates an admin user on startup via `DataInitializer.java`:

**Default Credentials:**
- Username: `admin`
- Password: `admin123`
- Role: `ROLE_ADMIN`

No manual setup required! ✅

---

## 📝 Example Request Flow

### 1. Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"pass123"}'
```

### 2. Create a habit
```bash
curl -X POST http://localhost:8080/api/habits \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Morning Run",
    "description":"5km jog every morning",
    "frequency":"DAILY",
    "targetCount":1
  }'
```

### 3. Complete habit
```bash
curl -X POST http://localhost:8080/api/habits/1/complete \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. View statistics
```bash
curl -X GET http://localhost:8080/api/habits/stats \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## ⚙️ Configuration

Key settings in `application.properties`:
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/habit_tracker
spring.datasource.username=habituser
spring.datasource.password=habitpass
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=mySecretKeyForJWT123456789ThisIsAVeryLongSecretKey
jwt.expiration=86400000  # 24 hours in milliseconds

# Logging
logging.level.com.habittracker=DEBUG
logging.file.name=logs/habit-tracker.log

# Cache
spring.cache.type=simple
```

---

## 🧪 Testing

Run all tests:
```bash
mvn test
```

Use **Postman**, **Insomnia**, or **Swagger UI** to test the API.

**Tip:** Import the Swagger JSON into Postman for automatic collection generation:
```
http://localhost:8080/v3/api-docs
```

---

## 🐛 Troubleshooting

### Issue: 403 Forbidden on protected endpoints

**Solution:** Make sure the JWT token is included in the Authorization header:
```http
Authorization: Bearer YOUR_TOKEN_HERE
```

### Issue: Can't complete habit again

**Solution:** Habits can only be completed once per day. The system uses dates (not timestamps) to validate completions.

### Issue: Token expired

**Solution:** Tokens expire after 24 hours. Login again to get a new token.

### Issue: Docker container won't start

**Solution:** Make sure Docker Desktop is running and ports 3306/8080 are not in use:
```bash
docker-compose down
docker-compose up -d --build
```

---

## 📦 Dependencies

Major dependencies used:
```xml

    org.springframework.boot
    spring-boot-starter-web



    org.springframework.boot
    spring-boot-starter-security



    org.springframework.boot
    spring-boot-starter-data-jpa



    io.jsonwebtoken
    jjwt-api
    0.12.3



    org.springdoc
    springdoc-openapi-starter-webmvc-ui
    2.3.0



    org.springframework.boot
    spring-boot-starter-cache

```

---

## 👨‍💻 Author

**Christopher**

- GitHub: [@christo256](https://github.com/christo256)

---

## 📄 License

This project is open source and available for educational purposes.

**Built using Spring Boot**
