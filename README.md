# Spring Boot Demo API

This is a Spring Boot demonstration application that provides various API endpoints with different behaviors and database interactions.

## Features

- **Normal API**: Standard API call with moderate response time
- **Error API**: Simulates error conditions
- **Slow API**: Simulates slow database operations (2-5 seconds)
- **Fast API**: Simulates fast database operations (10-50ms)
- **Random API**: Random behavior - could be fast, slow, or error
- **Stats API**: Provides statistics about all API calls

## Technologies Used

- Spring Boot 3.2.0
- Spring Data JPA
- H2 In-Memory Database
- Maven
- Java 17

## API Endpoints

### 1. Normal Call
```
GET /api/normal
```
Returns a standard response with moderate processing time (100-300ms).

### 2. Error Call
```
GET /api/error
```
Always returns an HTTP 500 error to simulate error conditions.

### 3. Slow Call
```
GET /api/slow
```
Simulates a slow database operation (2-5 seconds response time).

### 4. Fast Call
```
GET /api/fast
```
Simulates a fast database operation (10-50ms response time).

### 5. Random Call
```
GET /api/random
```
Random behavior - could be fast, slow, or result in an error.

### 6. Stats Call
```
GET /api/stats
```
Returns statistics about all previous API calls including counts by type and recent calls.

## Response Format

All successful API calls return a JSON response with the following structure:

```json
{
  "message": "Description of the operation",
  "callType": "API_TYPE",
  "endpoint": "/api/endpoint",
  "executionTimeMs": 123,
  "timestamp": "2025-10-31T10:30:00",
  "success": true
}
```

## Database

The application uses an H2 in-memory database to store information about each API call:

- **Endpoint**: The API endpoint that was called
- **Call Type**: Type of call (NORMAL, ERROR, SLOW, FAST, RANDOM, STATS)
- **Response Message**: The message returned by the API
- **Execution Time**: How long the operation took in milliseconds
- **Created At**: Timestamp of when the call was made

## Running the Application

1. **Prerequisites**: Java 17 and Maven installed

2. **Clone and navigate to the project directory**

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**:
   - API Base URL: `http://localhost:8080/api`
   - H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`)

## Example Usage

```bash
# Normal call
curl http://localhost:8080/api/normal

# Error call
curl http://localhost:8080/api/error

# Slow call
curl http://localhost:8080/api/slow

# Fast call
curl http://localhost:8080/api/fast

# Random call
curl http://localhost:8080/api/random

# Get statistics
curl http://localhost:8080/api/stats
```

## Database Schema

The application automatically creates the following table:

```sql
CREATE TABLE api_calls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    endpoint VARCHAR(255),
    call_type VARCHAR(50),
    response_message VARCHAR(500),
    execution_time_ms BIGINT,
    created_at TIMESTAMP
);
```

You can view and query the data using the H2 console at `http://localhost:8080/h2-console`.