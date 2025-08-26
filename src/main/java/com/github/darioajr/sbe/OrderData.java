package com.github.darioajr.sbe;

/**
 * Data class representing an Order message
 */
public record OrderData(
        long orderId,
        String symbol,
        Side side,
        long quantity,
        long price,
        long timestamp,
        BooleanType isActive,
        String clientOrderId
) {
    public OrderData {
        if (symbol == null || symbol.length() > 8) {
            throw new IllegalArgumentException("Symbol must be non-null and max 8 characters");
        }
        if (clientOrderId == null) {
            throw new IllegalArgumentException("Client order ID cannot be null");
        }
    }
}
