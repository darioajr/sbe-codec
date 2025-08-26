#!/bin/bash

# Build script for multi-platform native images using Docker

set -e

echo "Building SBE Encoder/Decoder Native Images for Multiple Platforms..."

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    echo "Error: Docker not found. Please install Docker."
    exit 1
fi

# Clean and compile first
./mvnw clean compile test

# Create buildx builder if not exists
docker buildx create --name multiplatform --use --bootstrap 2>/dev/null || docker buildx use multiplatform

# Build for Linux AMD64
echo "Building for Linux AMD64..."
docker buildx build --platform linux/amd64 -f Dockerfile.native -t sbe-encoder-decoder:linux-amd64 --load .

# Build for Linux ARM64
echo "Building for Linux ARM64..."
docker buildx build --platform linux/arm64 -f Dockerfile.native -t sbe-encoder-decoder:linux-arm64 --load .

# Build for macOS (if running on macOS with Apple Silicon support)
if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "Building for macOS ARM64..."
    docker buildx build --platform linux/arm64 -f Dockerfile.native -t sbe-encoder-decoder:macos-arm64 --load .
fi

# Extract binaries from containers
echo "Extracting Linux AMD64 binary..."
docker create --name temp-linux-amd64 sbe-encoder-decoder:linux-amd64
docker cp temp-linux-amd64:/app/sbe-encoder-decoder ./target/sbe-encoder-decoder-linux-amd64
docker rm temp-linux-amd64

echo "Extracting Linux ARM64 binary..."
docker create --name temp-linux-arm64 sbe-encoder-decoder:linux-arm64
docker cp temp-linux-arm64:/app/sbe-encoder-decoder ./target/sbe-encoder-decoder-linux-arm64
docker rm temp-linux-arm64

if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "Extracting macOS ARM64 binary..."
    docker create --name temp-macos-arm64 sbe-encoder-decoder:macos-arm64
    docker cp temp-macos-arm64:/app/sbe-encoder-decoder ./target/sbe-encoder-decoder-macos-arm64
    docker rm temp-macos-arm64
fi

echo "Multi-platform native builds completed successfully!"
echo "Binaries available:"
echo "- target/sbe-encoder-decoder-linux-amd64"
echo "- target/sbe-encoder-decoder-linux-arm64"
if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "- target/sbe-encoder-decoder-macos-arm64"
fi
