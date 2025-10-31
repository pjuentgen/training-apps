#!/bin/bash

# Web Frontend Demo Script for Spring Boot Application

echo "🚀 Spring Boot Web Frontend Demo"
echo "================================="
echo ""

# Check if application is running
echo "📡 Checking if Spring Boot application is running..."
if curl -s http://localhost:8080/api/stats > /dev/null; then
    echo "✅ Application is running on http://localhost:8080"
else
    echo "❌ Application is not running. Please start it first:"
    echo "   java -jar target/spring-boot-demo-0.0.1-SNAPSHOT.jar"
    exit 1
fi

echo ""
echo "🌐 Available Web Interfaces:"
echo "=============================="
echo ""

echo "1. 🖥️  Main Dashboard (Desktop)"
echo "   URL: http://localhost:8080/"
echo "   Features: Interactive dashboard with full API testing capabilities"
echo "   - Modern responsive design"
echo "   - Real-time API testing"
echo "   - Detailed response visualization"
echo "   - Statistics charts and recent calls"
echo "   - Database console access"
echo ""

echo "2. 📱 Mobile Interface"
echo "   URL: http://localhost:8080/mobile.html"
echo "   Features: Optimized for mobile devices"
echo "   - Touch-friendly interface"
echo "   - Compact API buttons"
echo "   - Mobile-optimized response display"
echo "   - Quick statistics view"
echo ""

echo "3. 📚 API Documentation"
echo "   URL: http://localhost:8080/docs.html"
echo "   Features: Complete API reference"
echo "   - Detailed endpoint documentation"
echo "   - Request/response examples"
echo "   - Database schema information"
echo "   - cURL examples"
echo "   - Interactive links to test endpoints"
echo ""

echo "4. 🗄️  Database Console"
echo "   URL: http://localhost:8080/h2-console"
echo "   Features: Direct database access"
echo "   - View all API call logs"
echo "   - Execute custom SQL queries"
echo "   - Real-time data monitoring"
echo ""

echo "🔗 Quick Access URLs:"
echo "===================="
echo "Main Dashboard:    http://localhost:8080/"
echo "Mobile Interface:  http://localhost:8080/mobile.html"
echo "Documentation:     http://localhost:8080/docs.html"
echo "Database Console:  http://localhost:8080/h2-console"
echo ""

# Test a few API calls to populate the database
echo "🧪 Running quick API tests to populate database..."
echo ""

echo "Testing Normal API..."
curl -s http://localhost:8080/api/normal > /dev/null

echo "Testing Fast API..."
curl -s http://localhost:8080/api/fast > /dev/null

echo "Testing Error API..."
curl -s http://localhost:8080/api/error > /dev/null

echo "Testing Random API..."
curl -s http://localhost:8080/api/random > /dev/null

echo ""
echo "✅ Test data created! You can now explore the web interfaces."
echo ""

# Get current statistics
echo "📊 Current Statistics:"
echo "===================="
curl -s http://localhost:8080/api/stats | jq '{
    totalCalls: .totalCalls,
    normalCalls: .normalCalls,
    errorCalls: .errorCalls,
    fastCalls: .fastCalls,
    randomCalls: .randomCalls
}'

echo ""
echo "🎯 Next Steps:"
echo "=============="
echo "1. Open http://localhost:8080/ in your browser for the main dashboard"
echo "2. Try the mobile interface at http://localhost:8080/mobile.html"
echo "3. Read the API docs at http://localhost:8080/docs.html"
echo "4. Explore the database at http://localhost:8080/h2-console"
echo "   (JDBC URL: jdbc:h2:mem:testdb, User: sa, Password: password)"
echo ""
echo "🔥 Features to Try:"
echo "=================="
echo "- Click API buttons to test different endpoints"
echo "- Watch real-time response data and timing"
echo "- View statistics and recent call history"
echo "- Test on mobile devices with the mobile interface"
echo "- Query the database directly for detailed analytics"
echo ""

# Open browser if on macOS
if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "🌐 Opening main dashboard in your default browser..."
    open http://localhost:8080/
fi

echo "✨ Demo complete! The web frontend is ready for exploration."