package com.github.darioajr.sbe;

import java.util.List;

/**
 * Simple JSON formatter for SBE data objects
 * Converts Order, Trade, and MarketData objects to JSON format
 */
public class JsonFormatter {
    
    /**
     * Convert any SBE data object to JSON string
     */
    public static String toJson(Object obj) {
        if (obj instanceof OrderData order) {
            return formatOrder(order);
        } else if (obj instanceof TradeData trade) {
            return formatTrade(trade);
        } else if (obj instanceof MarketDataData marketData) {
            return formatMarketData(marketData);
        } else {
            throw new IllegalArgumentException("Unsupported object type: " + obj.getClass().getSimpleName());
        }
    }
    
    /**
     * Format OrderData as JSON
     */
    public static String formatOrder(OrderData order) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"orderId\": ").append(order.orderId()).append(",\n");
        sb.append("  \"symbol\": \"").append(order.symbol()).append("\",\n");
        sb.append("  \"side\": \"").append(order.side()).append("\",\n");
        sb.append("  \"quantity\": ").append(order.quantity()).append(",\n");
        sb.append("  \"price\": ").append(order.price()).append(",\n");
        sb.append("  \"timestamp\": ").append(order.timestamp()).append(",\n");
        sb.append("  \"isActive\": \"").append(order.isActive()).append("\",\n");
        sb.append("  \"clientOrderId\": \"").append(order.clientOrderId()).append("\"\n");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Format TradeData as JSON
     */
    public static String formatTrade(TradeData trade) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"tradeId\": ").append(trade.tradeId()).append(",\n");
        sb.append("  \"orderId\": ").append(trade.orderId()).append(",\n");
        sb.append("  \"symbol\": \"").append(trade.symbol()).append("\",\n");
        sb.append("  \"side\": \"").append(trade.side()).append("\",\n");
        sb.append("  \"quantity\": ").append(trade.quantity()).append(",\n");
        sb.append("  \"price\": ").append(trade.price()).append(",\n");
        sb.append("  \"timestamp\": ").append(trade.timestamp()).append(",\n");
        sb.append("  \"venue\": \"").append(trade.venue()).append("\"\n");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Format MarketDataData as JSON
     */
    public static String formatMarketData(MarketDataData marketData) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"symbol\": \"").append(marketData.symbol()).append("\",\n");
        sb.append("  \"timestamp\": ").append(marketData.timestamp()).append(",\n");
        sb.append("  \"bidPrice\": ").append(marketData.bidPrice()).append(",\n");
        sb.append("  \"bidSize\": ").append(marketData.bidSize()).append(",\n");
        sb.append("  \"askPrice\": ").append(marketData.askPrice()).append(",\n");
        sb.append("  \"askSize\": ").append(marketData.askSize()).append(",\n");
        sb.append("  \"lastPrice\": ").append(marketData.lastPrice()).append(",\n");
        sb.append("  \"lastSize\": ").append(marketData.lastSize()).append(",\n");
        sb.append("  \"levels\": [\n");
        
        List<PriceLevelData> levels = marketData.levels();
        for (int i = 0; i < levels.size(); i++) {
            PriceLevelData level = levels.get(i);
            sb.append("    {\n");
            sb.append("      \"price\": ").append(level.price()).append(",\n");
            sb.append("      \"size\": ").append(level.size()).append(",\n");
            sb.append("      \"side\": \"").append(level.side()).append("\"\n");
            sb.append("    }");
            if (i < levels.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        
        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }
}
