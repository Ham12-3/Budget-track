#!/bin/bash

# Expense Tracker - Quick Start Script
# This script helps you quickly start the application

echo "🚀 Expense Tracker - Starting Application"
echo "========================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    echo "Visit: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    echo "Visit: https://docs.docker.com/compose/install/"
    exit 1
fi

# Function to start with Docker
start_docker() {
    echo "📦 Starting with Docker Compose..."
    docker-compose up -d --build
    
    echo ""
    echo "✅ Application started successfully!"
    echo ""
    echo "🌐 Access the application:"
    echo "   Frontend: http://localhost"
    echo "   Backend API: http://localhost:8080/api"
    echo "   H2 Console: http://localhost:8080/api/h2-console"
    echo ""
    echo "📊 Default credentials:"
    echo "   Username: johndoe"
    echo "   Email: john.doe@example.com"
    echo ""
    echo "🛑 To stop the application: docker-compose down"
    echo "📋 To view logs: docker-compose logs -f"
}

# Function to start in development mode
start_dev() {
    echo "🔧 Starting in development mode..."
    echo ""
    
    # Start backend
    echo "Starting backend..."
    cd expense-tracker-backend
    mvn spring-boot:run &
    BACKEND_PID=$!
    cd ..
    
    # Wait for backend to start
    echo "Waiting for backend to start..."
    sleep 10
    
    # Start frontend
    echo "Starting frontend..."
    cd expense-tracker-frontend
    npm install
    npm run dev &
    FRONTEND_PID=$!
    cd ..
    
    echo ""
    echo "✅ Development servers started!"
    echo ""
    echo "🌐 Access the application:"
    echo "   Frontend: http://localhost:5173"
    echo "   Backend API: http://localhost:8080/api"
    echo ""
    echo "Press Ctrl+C to stop both servers"
    
    # Wait for user to press Ctrl+C
    trap "kill $BACKEND_PID $FRONTEND_PID; exit" INT
    wait
}

# Main menu
echo "Choose how to start the application:"
echo "1) Docker (Recommended)"
echo "2) Development Mode"
echo "3) Exit"
echo ""
read -p "Enter your choice (1-3): " choice

case $choice in
    1)
        start_docker
        ;;
    2)
        start_dev
        ;;
    3)
        echo "Exiting..."
        exit 0
        ;;
    *)
        echo "Invalid choice. Please run the script again."
        exit 1
        ;;
esac