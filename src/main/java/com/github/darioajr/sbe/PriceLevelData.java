package com.github.darioajr.sbe;

/**
 * Data class representing a price level in market data
 */
public record PriceLevelData(
        long price,
        long size,
        Side side
) {
}
