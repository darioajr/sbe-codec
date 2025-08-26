package com.github.darioajr.sbe;

import java.util.Arrays;
import java.util.List;

/**
 * Main application demonstrating SBE serialization and deserialization
 */
public class SbeApplication {
    
    public static void main(String[] args) {
        System.out.println("SBE Encoder/Decoder Application");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Native Image: " + (System.getProperty("org.graalvm.nativeimage.imagecode") != null));
        
        SbeSerializer serializer = new SbeSerializer();
        SbeDeserializer deserializer = new SbeDeserializer();
        
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
