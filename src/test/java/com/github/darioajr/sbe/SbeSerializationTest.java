package com.github.darioajr.sbe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SbeSerializationTest {

    private SbeSerializer serializer;
    private SbeDeserializer deserializer;

    @BeforeEach
    void setUp() {
        serializer = new SbeSerializer();
        deserializer = new SbeDeserializer();
    }

    @Test
    void testOrderSerialization() {
        // Given
        OrderData originalOrder = new OrderData(
                12345L,
                "AAPL",
                Side.BUY,
                1000L,
                15000L,
                System.currentTimeMillis(),
                BooleanType.TRUE,
                "CLIENT-ORDER-001"
        );

        // When
        byte[] serializedData = serializer.serializeOrder(originalOrder);
        OrderData deserializedOrder = deserializer.deserializeOrder(serializedData);

        // Then
        assertNotNull(serializedData);
        assertTrue(serializedData.length > 0);
        assertEquals(originalOrder, deserializedOrder);
    }

    @Test
    void testTradeSerialization() {
        // Given
        TradeData originalTrade = new TradeData(
                67890L,
                12345L,
                "AAPL",
                Side.SELL,
                500L,
                14950L,
                System.currentTimeMillis(),
                "NASDAQ"
        );

        // When
        byte[] serializedData = serializer.serializeTrade(originalTrade);
        TradeData deserializedTrade = deserializer.deserializeTrade(serializedData);

        // Then
        assertNotNull(serializedData);
        assertTrue(serializedData.length > 0);
        assertEquals(originalTrade, deserializedTrade);
    }

    @Test
    void testMarketDataSerialization() {
        // Given
        List<PriceLevelData> levels = Arrays.asList(
                new PriceLevelData(14900L, 1000L, Side.BUY),
                new PriceLevelData(14950L, 2000L, Side.BUY),
                new PriceLevelData(15050L, 1500L, Side.SELL),
                new PriceLevelData(15100L, 800L, Side.SELL)
        );

        MarketDataData originalMarketData = new MarketDataData(
                "AAPL",
                System.currentTimeMillis(),
                14950L,
                2000L,
                15050L,
                1500L,
                15000L,
                500L,
                levels
        );

        // When
        byte[] serializedData = serializer.serializeMarketData(originalMarketData);
        MarketDataData deserializedMarketData = deserializer.deserializeMarketData(serializedData);

        // Then
        assertNotNull(serializedData);
        assertTrue(serializedData.length > 0);
        assertEquals(originalMarketData, deserializedMarketData);
        assertEquals(4, deserializedMarketData.levels().size());
    }

    @Test
    void testGenericDeserialization() {
        // Given
        OrderData originalOrder = new OrderData(
                99999L,
                "MSFT",
                Side.BUY,
                2000L,
                30000L,
                System.currentTimeMillis(),
                BooleanType.FALSE,
                "GENERIC-TEST"
        );

        // When
        byte[] serializedData = serializer.serializeOrder(originalOrder);
        Object deserializedObject = deserializer.deserialize(serializedData);

        // Then
        assertInstanceOf(OrderData.class, deserializedObject);
        OrderData deserializedOrder = (OrderData) deserializedObject;
        assertEquals(originalOrder, deserializedOrder);
    }

    @Test
    void testSerializationPerformance() {
        // Given
        OrderData order = new OrderData(
                1L, "TEST", Side.BUY, 100L, 1000L,
                System.currentTimeMillis(), BooleanType.TRUE, "PERF-TEST"
        );

        // When - measure serialization performance
        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            byte[] data = serializer.serializeOrder(order);
            deserializer.deserializeOrder(data);
        }
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        // Then
        System.out.println("10,000 serialize/deserialize cycles took: " + durationMs + "ms");
        assertTrue(durationMs < 5000, "Performance should be under 5 seconds for 10k cycles");
    }

    @Test
    void testInvalidSymbolLength() {
        // Given/When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderData(1L, "TOOLONGSYMBOL", Side.BUY, 100L, 1000L,
                    System.currentTimeMillis(), BooleanType.TRUE, "TEST");
        });
    }

    @Test
    void testNullClientOrderId() {
        // Given/When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderData(1L, "TEST", Side.BUY, 100L, 1000L,
                    System.currentTimeMillis(), BooleanType.TRUE, null);
        });
    }
}
