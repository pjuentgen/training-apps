#!/bin/bash

# Demo script to test all Spring Boot API endpoints

echo "=== Spring Boot Demo API Test ==="
echo ""

# Start the application in the background
echo "Starting Spring Boot application..."
cd /Users/pju/repos/private/training-apps
java -jar target/spring-boot-demo-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
APP_PID=$!

# Wait for application to start
echo "Waiting for application to start..."
sleep 10

# Test all endpoints
echo ""
echo "Testing all API endpoints..."
echo ""

echo "1. Normal API Call:"
echo "Request: GET /api/normal"
curl -s http://localhost:8080/api/normal | jq .
echo ""
echo ""

echo "2. Error API Call:"
echo "Request: GET /api/error"
curl -s http://localhost:8080/api/error | jq .
echo ""
echo ""

echo "3. Fast API Call:"
echo "Request: GET /api/fast"
curl -s http://localhost:8080/api/fast | jq .
echo ""
echo ""

echo "4. Slow API Call (this will take a few seconds):"
echo "Request: GET /api/slow"
curl -s http://localhost:8080/api/slow | jq .
echo ""
echo ""

echo "5. Random API Call:"
echo "Request: GET /api/random"
curl -s http://localhost:8080/api/random | jq .
echo ""
echo ""

echo "6. Statistics API Call:"
echo "Request: GET /api/stats"
curl -s http://localhost:8080/api/stats | jq .
echo ""
echo ""

echo "=== Test Complete ==="
echo "Stopping application..."
kill $APP_PID
echo "Application stopped."
echo ""
echo "The H2 database console was available at: http://localhost:8080/h2-console"
echo "JDBC URL: jdbc:h2:mem:testdb"
echo "Username: sa"
echo "Password: password"