# SBE Encoder/Decoder with GraalVM Native Image Support

A high-performance Java 21 application for serializing and deserializing messages using Simple Binary Encoding (SBE) with support for GraalVM native image compilation.

**Repository**: [github.com/darioajr/sbe-codec](https://github.com/darioajr/sbe-codec)

## Features

- **Java 21**: Latest LTS Java version with modern language features
- **SBE (Simple Binary Encoding)**: Ultra-fast binary serialization
- **GraalVM Native Image**: Compile to native executables for faster startup and lower memory usage
- **Docker Support**: Both Java and native image Docker builds
- **Cross-Platform**: Windows and Linux build scripts
- **Comprehensive Testing**: Unit tests with performance benchmarks

## Project Structure

```
sbe_encoder_decoder/
├── src/
│   ├── main/
│   │   ├── java/com/github/darioajr/sbe/
│   │   │   ├── SbeApplication.java       # Main application
│   │   │   ├── SbeSerializer.java        # Message serializer
│   │   │   ├── SbeDeserializer.java      # Message deserializer
│   │   │   ├── OrderData.java            # Order data record
│   │   │   ├── TradeData.java            # Trade data record
│   │   │   ├── MarketDataData.java       # Market data record
│   │   │   ├── PriceLevelData.java       # Price level record
│   │   │   ├── BooleanType.java          # Boolean enum type
│   │   │   ├── Side.java                 # Side enum (BUY/SELL)
│   │   │   ├── *Encoder.java             # SBE message encoders
│   │   │   ├── *Decoder.java             # SBE message decoders
│   │   │   └── MessageHeader*.java       # SBE message headers
│   │   └── resources/
│   │       ├── sbe/message-schema.xml    # SBE schema definition
│   │       └── META-INF/native-image/    # GraalVM configuration
│   └── test/
│       └── java/com/github/darioajr/sbe/
│           └── SbeSerializationTest.java # Comprehensive tests
├── build-scripts/                       # Build scripts for different platforms
├── Dockerfile.java                      # Docker build for Java
├── Dockerfile.native                    # Docker build for native image
├── docker-compose.yml                   # Docker Compose configuration
└── pom.xml                              # Maven configuration
```

## Message Types

The application supports three message types:

1. **Order**: Trading orders with client order ID
2. **Trade**: Trade executions with venue information
3. **MarketData**: Market data snapshots with price levels

## Quick Start

### Prerequisites

- Java 21 or later
- Maven 3.6+
- Docker (optional)
- GraalVM (for native builds)

### Build and Run (Java)

```bash
# Windows
build-scripts\build-java.bat

# Linux/Mac
chmod +x build-scripts/build-java.sh
./build-scripts/build-java.sh
```

### Building with Docker (Recommended for Native Image)

Build native image using Docker without requiring local GraalVM installation:

```bash
# Linux/macOS
./build-scripts/build-native.sh

# Windows
build-scripts\build-native.bat
```

The Docker build uses GraalVM Community Edition and creates a native executable in the `target/` directory.

### Docker Builds

```bash
# Build and run Java version
docker-compose up sbe-java

# Build and run native version
docker-compose up sbe-native

# Build both versions
docker-compose up
```

## Usage Examples

### Basic Serialization/Deserialization

```java
import com.github.darioajr.sbe.*;

// Create serializer and deserializer
SbeSerializer serializer = new SbeSerializer();
SbeDeserializer deserializer = new SbeDeserializer();

// Create and serialize an order
OrderData order = new OrderData(
    12345L,                    // orderId
    "AAPL",                   // symbol
    Side.BUY,                 // side
    1000L,                    // quantity
    15000L,                   // price (in cents)
    System.currentTimeMillis(), // timestamp
    BooleanType.TRUE,         // isActive
    "CLIENT123"               // clientOrderId
);

byte[] serializedOrder = serializer.serializeOrder(order);

// Deserialize back to object
OrderData deserializedOrder = (OrderData) deserializer.deserialize(serializedOrder);
```

## Performance

SBE provides exceptional performance characteristics:

- **Serialization**: ~10,000 serialize/deserialize cycles in under 5 seconds
- **Memory Efficient**: Zero-copy deserialization where possible
- **Native Image**: Sub-second startup times with lower memory footprint

## SBE Schema

The schema defines three message types with the following fields:

### Order Message (ID: 1)
- orderId, symbol, side, quantity, price, timestamp, isActive
- Variable length clientOrderId

### Trade Message (ID: 2)
- tradeId, orderId, symbol, side, quantity, price, timestamp
- Variable length venue

### MarketData Message (ID: 3)
- symbol, timestamp, bid/ask prices and sizes, last price/size
- Repeating group of price levels

## Development

### Project Structure

The project follows standard Maven conventions with all SBE classes in the main package:

- **Data Classes**: `OrderData`, `TradeData`, `MarketDataData`, `PriceLevelData`
- **Serialization**: `SbeSerializer` handles encoding to binary format
- **Deserialization**: `SbeDeserializer` handles decoding from binary format
- **SBE Classes**: Message encoders/decoders, headers, and enums in `com.github.darioajr.sbe`
- **Enums**: `Side` (BUY/SELL), `BooleanType` (TRUE/FALSE)

## Native Image Configuration

The project includes comprehensive GraalVM native image configuration:

- **Reflection Config**: Pre-configured for all SBE classes
- **Resource Config**: Includes SBE schema files
- **Build Args**: Optimized for performance and compatibility

## Docker Images

### Java Image
- Based on Eclipse Temurin 21
- Multi-stage build for optimal size
- Non-root user for security
- Health checks included

### Native Image
- Based on GraalVM Community Edition
- Distroless runtime for minimal attack surface
- Ultra-fast startup times
- Reduced memory footprint

## Testing

Run the comprehensive test suite:

```bash
mvn test
```

Tests include:
- Serialization/deserialization correctness
- Performance benchmarks
- Error handling
- Data validation

## Build Profiles

- **Default**: Standard Java compilation
- **Native**: GraalVM native image compilation

## Contributing

1. Ensure Java 21+ is installed
2. Run tests: `mvn test`
3. Build both versions to verify compatibility
4. Follow existing code patterns and naming conventions

## License

This project is provided as an example implementation of SBE with GraalVM native image support.
