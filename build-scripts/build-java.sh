#!/bin/bash

# Build script for Java application

set -e

echo "Building SBE Encoder/Decoder Java Application..."

# Clean and compile
mvnw clean compile

# Generate SBE classes
mvnw generate-sources

# Run tests
mvnw test

# Package application
mvnw package

echo "Java build completed successfully!"
echo "JAR file: target/sbe-encoder-decoder-1.0.0.jar"

# Run the application
echo "Running application..."
java -jar target/sbe-encoder-decoder-1.0.0.jar
