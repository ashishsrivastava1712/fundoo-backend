# Fundoo Notes Backend

Backend for a notes application (similar in spirit to Google Keep), written in Java with Spring Boot. Users can register, log in with a JWT, and manage their own notes. On top of the basic note features, the project also wires in the infrastructure pieces that a real backend usually needs: Redis, ActiveMQ, RabbitMQ, Spring Cache, Spring Batch with Excel import, and AOP based logging.

I built this step by step, one feature branch at a time, and merged each branch into `develop` through a pull request. The `main` branch only holds this README and the `.gitignore`. The full source code lives on `develop`.

---

## What the application does

- Register a user and store the password as a BCrypt hash
- Log in and receive a JWT that is used for every protected request
- Create notes and list only the notes that belong to the logged in user
- Pin / unpin, archive / unarchive, and trash / restore a note
- Check note ownership before any change, so one user cannot touch another user's notes
- Return clean, consistent error responses from one global exception handler
- Publish a user registration event to ActiveMQ (JMS) and consume it with a listener
- Publish note reminder events to RabbitMQ through a direct exchange and routing key
- Generate and verify six digit OTPs stored in Redis with an expiry
- Cache note reads with Spring Cache and evict the cache when a note changes
- Run a Spring Batch job, and import notes in bulk from an `.xlsx` file using Apache POI
- Log service method calls and measure their execution time using Spring AOP

---

## Tech stack

| Area | Used |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.2 (Web MVC, Data JPA, Validation) |
| Database | MySQL 8 |
| Security | JWT (JJWT), BCrypt |
| Messaging | ActiveMQ with Spring JMS, RabbitMQ with Spring AMQP |
| Cache / key-value store | Redis, Spring Cache |
| Batch and files | Spring Batch, Apache POI |
| Cross-cutting | Spring AOP, SLF4J |
| Build | Maven (wrapper included) |
| Testing | JUnit 5, Mockito |
| Tools | IntelliJ IDEA, Postman, Docker |

---

## Project structure

```text
src/main/java/com/fundoo/notes
|-- FundooNotesApplication.java
|-- aspect        LoggingAspect, PerformanceAspect
|-- config        Batch, Cache, Excel import, JMS, RabbitMQ, Redis, Password, ObjectMapper
|-- controller    User, Note, Reminder, Redis, BatchJob, ExcelImport
|-- dto
|   |-- request   LoginRequestDto, UserRegisterRequestDto, NoteRequestDto, ReminderRequestDto, NoteExcelRow
|   |-- response  LoginResponseDto, UserResponseDto, NoteResponseDto, ApiErrorResponseDto
|-- entity        User, Note
|-- event         UserRegistrationEvent, ReminderEvent
|-- exception     GlobalExceptionHandler and custom exceptions
|-- repository    UserRepository, NoteRepository
|-- service       service interfaces
    |-- impl      service implementations, JMS / RabbitMQ producers and consumers
```

The flow of a request is the usual layered one:

```text
Postman -> Controller -> DTO validation -> Service -> Repository -> MySQL
                                              |
                                              +-> JWT service
                                              +-> Redis / Cache
                                              +-> ActiveMQ / RabbitMQ events
```

AOP aspects wrap the service layer, so every service call is logged along with how long it took.

---

## Branches

Each feature was developed in its own branch and merged into `develop`:

| Branch | Work done |
|---|---|
| feature/UserEntityRepository | User entity and repository |
| feature/DTO | Request and response DTOs with validation |
| feature/ServiceController | User service and controller (register, login) |
| feature/TokenGenAndEncoding | BCrypt password encoding and JWT generation |
| feature/ExceptionHandling | Global exception handler and custom exceptions |
| feature/NoteEntityAndRepository | Note entity, User to Note relationship, repository |
| feature/Note | Note APIs: create, list, pin, archive, trash |
| feature/LoggingStatement | Logging across the application |
| feature/JMSIntegration | ActiveMQ registration events |
| feature/RabbitMQ | Reminder events through RabbitMQ |
| feature/RedisImplementation | Redis key-value APIs and OTP |
| feature/Cache | Spring Cache on note reads |
| feature/BatchProcessing | Spring Batch job |
| feature/ExcelIntegration | Excel note import using Apache POI and Spring Batch |
| feature/AOP | Logging and performance aspects |

---

## API endpoints

Base URL: `http://localhost:8080`

Every endpoint except register and login needs this header:

```http
Authorization: Bearer <token from login>
```

### Users

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/users/register` | Create an account |
| POST | `/api/users/login` | Log in and get a JWT |

```json
{
  "firstName": "Ashish",
  "email": "ashish@example.com",
  "password": "Password123"
}
```

Login only needs `email` and `password`.

### Notes

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/notes` | Create a note |
| GET | `/api/notes` | Get all notes of the logged in user |
| PATCH | `/api/notes/{noteId}/pin` | Toggle pin |
| PATCH | `/api/notes/{noteId}/archive` | Toggle archive |
| PATCH | `/api/notes/{noteId}/trash` | Toggle trash |

```json
{
  "title": "Weekly plan",
  "description": "Finish the batch module and test the Excel import"
}
```

### Reminders (RabbitMQ)

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/reminders` | Publish a reminder event for a note |

```json
{
  "noteId": 1,
  "reminderTime": "2026-12-01T10:00:00"
}
```

`reminderTime` has to be in the future.

### Redis and OTP

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/redis/save?key=k&value=v` | Save a value |
| GET | `/api/redis/get/{key}` | Read a value |
| DELETE | `/api/redis/delete/{key}` | Delete a value |
| POST | `/api/redis/otp/generate?email=...` | Generate an OTP |
| POST | `/api/redis/otp/verify?email=...&otp=...` | Verify an OTP |

The generate endpoint returns the OTP in the response only to make testing in Postman easy. In a real deployment it would be sent by email or SMS instead.

### Batch and Excel import

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/batch/run` | Run the sample batch job |
| POST | `/api/batch/import/notes` | Import notes from an Excel file |

For the import, send `form-data` with key `file` (type File) and pick an `.xlsx` file. The sheet should have two columns: `Title` and `Description`.

---

## Database

Two tables are created automatically by Hibernate (`ddl-auto=update`):

- `users` - id, first name, email (unique), BCrypt password
- `notes` - id, title, description, pinned, archived, trashed, and the owning user

One user can have many notes; every note belongs to exactly one user.

---

## Running it locally

### 1. Requirements

- JDK 21 or newer
- MySQL 8
- Docker Desktop (for ActiveMQ, RabbitMQ and Redis)
- Postman

### 2. Get the code

```bash
git clone https://github.com/ashishsrivastava1712/fundoo-backend.git
cd fundoo-backend
git checkout develop
```

### 3. Create the database

```sql
CREATE DATABASE fundoo_notes;
```

Put your own MySQL username and password in `src/main/resources/application.properties`.

### 4. Set the JWT secret

The secret is read from an environment variable and is not stored in the repository. Use any Base64 string of at least 32 bytes.

```powershell
$env:JWT_SECRET = "<your-base64-secret>"
```

### 5. Start the supporting services

```bash
docker run -d --name fundoo-activemq -p 61616:61616 -p 8161:8161 apache/activemq-classic:6.1.7
docker run -d --name fundoo-rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management
docker run -d --name fundoo-redis -p 6379:6379 redis:7-alpine
```

| Service | Port |
|---|---|
| Application | 8080 |
| MySQL | 3306 |
| ActiveMQ / console | 61616 / 8161 (admin / admin) |
| RabbitMQ / console | 5672 / 15672 (guest / guest) |
| Redis | 6379 |

### 6. Build, test and run

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Or just run `FundooNotesApplication` from IntelliJ.

---

## Testing order in Postman

1. Register a user
2. Log in and copy the token
3. Create two or three notes
4. Get all notes
5. Pin, archive and trash a note, then get the notes again to see the flags change
6. Generate an OTP and verify it
7. Create a reminder and check the RabbitMQ consumer log in the console
8. Check the console for the JMS registration event from step 1
9. Run the batch job
10. Import an Excel file and get the notes again to see the imported ones
11. Look at the console for the AOP logs with execution times

---

## Things that are not done yet

- Real email / SMS delivery for OTPs and reminders (right now the consumers only log the event)
- Updating and deleting notes, labels, search
- Logout with token blacklisting
- Retry and dead letter handling for failed messages
- Moving the cache from the simple in-memory cache to Redis
- A frontend

---

## Author

Ashish Srivastava
GitHub: https://github.com/ashishsrivastava1712
