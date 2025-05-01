#!/bin/sh
set -e

echo "Waiting for PostgreSQL to start..."
sleep 10

echo "PostgreSQL started, launching application..."
exec java -jar /app.jar --spring.datasource.url=jdbc:postgresql://db:5432/challenger
