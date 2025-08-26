package com.github.darioajr.sbe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Command-line utility for SBE serialization and deserialization
 * 
 * Usage:
 *   java SbeApplication serialize <type> <input-file> <output-file>
 *   java SbeApplication deserialize <input-file> [output-file]
 *   java SbeApplication demo
 * 
 * Types: order, trade, marketdata
 */
public class SbeApplication {
    
    private static final SbeSerializer serializer = new SbeSerializer();
    private static final SbeDeserializer deserializer = new SbeDeserializer();
    
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        String command = args[0].toLowerCase();
        
        try {
            switch (command) {
                case "serialize" -> handleSerialize(args);
                case "deserialize" -> handleDeserialize(args);
                case "demo" -> runDemo();
                case "help", "-h", "--help" -> printUsage();
                default -> {
                    System.err.println("Unknown command: " + command);
                    printUsage();
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void printUsage() {
        System.out.println("SBE Encoder/Decoder Command-Line Utility");
        System.out.println("Usage:");
        System.out.println("  serialize <type> <input-json> <output-binary>");
        System.out.println("  deserialize <input-binary> [output-json]");
        System.out.println("  demo");
        System.out.println("  help");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  serialize   - Convert JSON data to SBE binary format");
        System.out.println("  deserialize - Convert SBE binary data to JSON format");
        System.out.println("  demo        - Run demonstration examples");
        System.out.println("  help        - Show this help message");
        System.out.println();
        System.out.println("Types for serialize:");
        System.out.println("  order       - Serialize order data");
        System.out.println("  trade       - Serialize trade data");
        System.out.println("  marketdata  - Serialize market data");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  serialize order order.json order.sbe");
        System.out.println("  deserialize order.sbe order_output.json");
        System.out.println("  demo");
    }
    
    private static void handleSerialize(String[] args) throws IOException {
        if (args.length != 4) {
            System.err.println("Usage: serialize <type> <input-json> <output-binary>");
            System.exit(1);
        }
        
        String type = args[1].toLowerCase();
        String inputFile = args[2];
        String outputFile = args[3];
        
        Path inputPath = Paths.get(inputFile);
        if (!Files.exists(inputPath)) {
            System.err.println("Input file does not exist: " + inputFile);
            System.exit(1);
        }
        
        String jsonContent = Files.readString(inputPath);
        byte[] serializedData;
        
        switch (type) {
            case "order" -> {
                OrderData orderData = JsonParser.parseOrder(jsonContent);
                serializedData = serializer.serializeOrder(orderData);
                System.out.println("Serialized Order: " + orderData);
            }
            case "trade" -> {
                TradeData tradeData = JsonParser.parseTrade(jsonContent);
                serializedData = serializer.serializeTrade(tradeData);
                System.out.println("Serialized Trade: " + tradeData);
            }
            case "marketdata" -> {
                MarketDataData marketData = JsonParser.parseMarketData(jsonContent);
                serializedData = serializer.serializeMarketData(marketData);
                System.out.println("Serialized MarketData: " + marketData);
            }
            default -> {
                System.err.println("Unknown type: " + type + ". Use: order, trade, or marketdata");
                System.exit(1);
                return;
            }
        }
        
        Files.write(Paths.get(outputFile), serializedData);
        System.out.println("Serialized data written to: " + outputFile + " (" + serializedData.length + " bytes)");
    }
    
    private static void handleDeserialize(String[] args) throws IOException {
        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: deserialize <input-binary> [output-json]");
            System.exit(1);
        }
        
        String inputFile = args[1];
        String outputFile = args.length == 3 ? args[2] : null;
        
        Path inputPath = Paths.get(inputFile);
        if (!Files.exists(inputPath)) {
            System.err.println("Input file does not exist: " + inputFile);
            System.exit(1);
        }
        
        byte[] binaryData = Files.readAllBytes(inputPath);
        Object deserializedObject = deserializer.deserialize(binaryData);
        
        String jsonOutput = JsonFormatter.toJson(deserializedObject);
        
        if (outputFile != null) {
            Files.writeString(Paths.get(outputFile), jsonOutput);
            System.out.println("Deserialized data written to: " + outputFile);
        } else {
            System.out.println("Deserialized data:");
            System.out.println(jsonOutput);
        }
        
        System.out.println("Deserialized object: " + deserializedObject);
    }
    
    private static void runDemo() {
        System.out.println("Running SBE demonstration examples...");
        System.out.println();
        
        // Example 1: Order serialization/deserialization
        demonstrateOrderSerialization(serializer, deserializer);
        
        // Example 2: Trade serialization/deserialization
        demonstrateTradeSerialization(serializer, deserializer);
        
        // Example 3: MarketData serialization/deserialization
        demonstrateMarketDataSerialization(serializer, deserializer);
        
        System.out.println("\nAll examples completed successfully!");
    }
    
    private static void demonstrateOrderSerialization(SbeSerializer serializer, SbeDeserializer deserializer) {
        System.out.println("\n=== Order Serialization Example ===");
        
        // Create sample order
        OrderData originalOrder = new OrderData(
                12345L,
                "AAPL",
                Side.BUY,
                1000L,
                15000L, // $150.00 in ticks
                System.currentTimeMillis(),
                BooleanType.TRUE,
                "CLIENT-ORDER-001"
        );
        
        System.out.println("Original Order: " + originalOrder);
        
        // Serialize
        byte[] serializedData = serializer.serializeOrder(originalOrder);
        System.out.println("Serialized size: " + serializedData.length + " bytes");
        
        // Deserialize
        OrderData deserializedOrder = deserializer.deserializeOrder(serializedData);
        System.out.println("Deserialized Order: " + deserializedOrder);
        
        // Verify
        if (originalOrder.equals(deserializedOrder)) {
            System.out.println("✓ Order serialization/deserialization successful!");
        } else {
            System.out.println("✗ Order serialization/deserialization failed!");
        }
    }
    
    private static void demonstrateTradeSerialization(SbeSerializer serializer, SbeDeserializer deserializer) {
        System.out.println("\n=== Trade Serialization Example ===");
        
        // Create sample trade
        TradeData originalTrade = new TradeData(
                67890L,
                12345L,
                "AAPL",
                Side.BUY,
                500L,
                14950L, // $149.50 in ticks
                System.currentTimeMillis(),
                "NASDAQ"
        );
        
        System.out.println("Original Trade: " + originalTrade);
        
        // Serialize
        byte[] serializedData = serializer.serializeTrade(originalTrade);
        System.out.println("Serialized size: " + serializedData.length + " bytes");
        
        // Deserialize
        TradeData deserializedTrade = deserializer.deserializeTrade(serializedData);
        System.out.println("Deserialized Trade: " + deserializedTrade);
        
        // Verify
        if (originalTrade.equals(deserializedTrade)) {
            System.out.println("✓ Trade serialization/deserialization successful!");
        } else {
            System.out.println("✗ Trade serialization/deserialization failed!");
        }
    }
    
    private static void demonstrateMarketDataSerialization(SbeSerializer serializer, SbeDeserializer deserializer) {
        System.out.println("\n=== MarketData Serialization Example ===");
        
        // Create sample market data with price levels
        List<PriceLevelData> levels = Arrays.asList(
                new PriceLevelData(14900L, 1000L, Side.BUY),
                new PriceLevelData(14950L, 2000L, Side.BUY),
                new PriceLevelData(15050L, 1500L, Side.SELL),
                new PriceLevelData(15100L, 800L, Side.SELL)
        );
        
        MarketDataData originalMarketData = new MarketDataData(
                "AAPL",
                System.currentTimeMillis(),
                14950L, // Best bid
                2000L,  // Best bid size
                15050L, // Best ask
                1500L,  // Best ask size
                15000L, // Last price
                500L,   // Last size
                levels
        );
        
        System.out.println("Original MarketData: " + originalMarketData);
        
        // Serialize
        byte[] serializedData = serializer.serializeMarketData(originalMarketData);
        System.out.println("Serialized size: " + serializedData.length + " bytes");
        
        // Deserialize
        MarketDataData deserializedMarketData = deserializer.deserializeMarketData(serializedData);
        System.out.println("Deserialized MarketData: " + deserializedMarketData);
        
        // Verify
        if (originalMarketData.equals(deserializedMarketData)) {
            System.out.println("✓ MarketData serialization/deserialization successful!");
        } else {
            System.out.println("✗ MarketData serialization/deserialization failed!");
        }
    }
}
