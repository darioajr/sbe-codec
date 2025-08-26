package com.github.darioajr.sbe;

import java.util.List;

/**
 * Data class representing a MarketData message
 */
public record MarketDataData(
        String symbol,
        long timestamp,
        long bidPrice,
        long bidSize,
        long askPrice,
        long askSize,
        long lastPrice,
        long lastSize,
        List<PriceLevelData> levels
) {
    public MarketDataData {
        if (symbol == null || symbol.length() > 8) {
            throw new IllegalArgumentException("Symbol must be non-null and max 8 characters");
        }
        if (levels == null) {
            throw new IllegalArgumentException("Levels cannot be null");
        }
    }
}
