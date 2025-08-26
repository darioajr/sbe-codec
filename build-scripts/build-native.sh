#!/bin/bash

# Build script for GraalVM native image using Docker

set -e

echo "Building SBE Encoder/Decoder Native Application with Docker..."

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    echo "Error: Docker not found. Please install Docker."
    exit 1
fi

# Clean and compile first
./mvnw clean compile test

# Build Docker image for native compilation
echo "Building Docker image..."
docker build -f Dockerfile.native -t sbe-encoder-decoder:native .

# Run Docker container to build native image
echo "Building native image in Docker container..."
docker run --rm -v "$(pwd)":/workspace -w /workspace sbe-encoder-decoder:native mvn package -Pnative -DskipTests

echo "Native build completed successfully!"
echo "Native executable: target/sbe-encoder-decoder"

# Test the native application in Docker
echo "Testing native application in Docker..."
docker run --rm -v "$(pwd)":/workspace -w /workspace sbe-encoder-decoder:native ./target/sbe-encoder-decoder
