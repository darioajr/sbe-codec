@echo off

REM Build script for Java application on Windows

echo Building SBE Encoder/Decoder Java Application...

REM Clean and compile
call mvnw clean compile
if %ERRORLEVEL% neq 0 (
    echo Error during compilation
    exit /b 1
)

REM Generate SBE classes
call mvnw generate-sources
if %ERRORLEVEL% neq 0 (
    echo Error during SBE generation
    exit /b 1
)

REM Run tests
call mvnw test
if %ERRORLEVEL% neq 0 (
    echo Error during testing
    exit /b 1
)

REM Package application
call mvnw package
if %ERRORLEVEL% neq 0 (
    echo Error during packaging
    exit /b 1
)

echo Java build completed successfully!
echo JAR file: target\sbe-encoder-decoder-1.0.0.jar

REM Run the application
echo Running application...
java -jar target\sbe-encoder-decoder-1.0.0.jar
