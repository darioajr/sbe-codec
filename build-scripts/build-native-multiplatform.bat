@echo off

REM Build script for multi-platform native images using Docker

echo Building SBE Encoder/Decoder Native Images for Multiple Platforms...

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

REM Create buildx builder if not exists
docker buildx create --name multiplatform --use --bootstrap 2>nul || docker buildx use multiplatform

REM Build for Linux AMD64
echo Building for Linux AMD64...
docker buildx build --platform linux/amd64 -f Dockerfile.native -t sbe-encoder-decoder:linux-amd64 --load .
if %ERRORLEVEL% neq 0 (
    echo Error building Linux AMD64 image
    exit /b 1
)

REM Build for Linux ARM64
echo Building for Linux ARM64...
docker buildx build --platform linux/arm64 -f Dockerfile.native -t sbe-encoder-decoder:linux-arm64 --load .
if %ERRORLEVEL% neq 0 (
    echo Error building Linux ARM64 image
    exit /b 1
)

REM Extract binaries from containers
echo Extracting Linux AMD64 binary...
docker create --name temp-linux-amd64 sbe-encoder-decoder:linux-amd64
docker cp temp-linux-amd64:/app/sbe-encoder-decoder ./target/sbe-encoder-decoder-linux-amd64
docker rm temp-linux-amd64

echo Extracting Linux ARM64 binary...
docker create --name temp-linux-arm64 sbe-encoder-decoder:linux-arm64
docker cp temp-linux-arm64:/app/sbe-encoder-decoder ./target/sbe-encoder-decoder-linux-arm64
docker rm temp-linux-arm64

echo Multi-platform native builds completed successfully!
echo Binaries available:
echo - target/sbe-encoder-decoder-linux-amd64
echo - target/sbe-encoder-decoder-linux-arm64
