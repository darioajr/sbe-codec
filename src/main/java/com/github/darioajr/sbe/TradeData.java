package com.github.darioajr.sbe;

/**
 * Data class representing a Trade message
 */
public record TradeData(
        long tradeId,
        long orderId,
        String symbol,
        Side side,
        long quantity,
        long price,
        long timestamp,
        String venue
) {
    public TradeData {
        if (symbol == null || symbol.length() > 8) {
            throw new IllegalArgumentException("Symbol must be non-null and max 8 characters");
        }
        if (venue == null) {
            throw new IllegalArgumentException("Venue cannot be null");
        }
    }
}
