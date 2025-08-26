@echo off

REM Build script for GraalVM native image using Docker on Windows

echo Building SBE Encoder/Decoder Native Application with Docker...

REM Check if Docker is available
docker --version >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Error: Docker not found. Please install Docker Desktop.
    exit /b 1
)

REM Clean and compile first
call mvnw clean compile test
if %ERRORLEVEL% neq 0 (
    echo Error during compilation/testing
    exit /b 1
)

REM Build Docker image for native compilation
echo Building Docker image...
docker build -f Dockerfile.native -t sbe-encoder-decoder:native .
if %ERRORLEVEL% neq 0 (
    echo Error building Docker image
    exit /b 1
)

REM Run Docker container to build native image
echo Building native image in Docker container...
docker run --rm -v "%cd%":/workspace -w /workspace sbe-encoder-decoder:native mvn package -Pnative -DskipTests
if %ERRORLEVEL% neq 0 (
    echo Error during native image build in Docker
    exit /b 1
)

echo Native build completed successfully!
echo Native executable: target\sbe-encoder-decoder

REM Test the native application in Docker
echo Testing native application in Docker...
docker run --rm -v "%cd%":/workspace -w /workspace sbe-encoder-decoder:native ./target/sbe-encoder-decoder
