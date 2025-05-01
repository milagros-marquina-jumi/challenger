#!/bin/bash

echo "Starting Challenger API..."
echo "This will build and start the application and database in Docker containers."

# Ensure Docker is running
if ! docker info > /dev/null 2>&1; then
  echo "Error: Docker is not running or not installed."
  echo "Please start Docker and try again."
  exit 1
fi

# Stop any existing containers
echo "Stopping any existing containers..."
docker-compose down

# Build and start the containers
echo "Building and starting containers..."
docker-compose up --build

# The script will continue running until the user presses Ctrl+C
# When that happens, we'll clean up
trap 'echo "Stopping containers..."; docker-compose down; echo "Containers stopped."; exit 0' INT

# Keep the script running
while true; do
  sleep 1
done
