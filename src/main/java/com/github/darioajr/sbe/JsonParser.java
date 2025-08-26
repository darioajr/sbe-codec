package com.github.darioajr.sbe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple JSON parser for SBE data objects
 * Supports parsing Order, Trade, and MarketData from JSON format
 */
public class JsonParser {
    
    /**
     * Parse Order data from JSON string
     */
    public static OrderData parseOrder(String json) {
        try {
            long orderId = extractLong(json, "orderId");
            String symbol = extractString(json, "symbol");
            Side side = Side.valueOf(extractString(json, "side"));
            long quantity = extractLong(json, "quantity");
            long price = extractLong(json, "price");
            long timestamp = extractLong(json, "timestamp");
            BooleanType isActive = BooleanType.valueOf(extractString(json, "isActive"));
            String clientOrderId = extractString(json, "clientOrderId");
            
            return new OrderData(orderId, symbol, side, quantity, price, timestamp, isActive, clientOrderId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Order JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parse Trade data from JSON string
     */
    public static TradeData parseTrade(String json) {
        try {
            long tradeId = extractLong(json, "tradeId");
            long orderId = extractLong(json, "orderId");
            String symbol = extractString(json, "symbol");
            Side side = Side.valueOf(extractString(json, "side"));
            long quantity = extractLong(json, "quantity");
            long price = extractLong(json, "price");
            long timestamp = extractLong(json, "timestamp");
            String venue = extractString(json, "venue");
            
            return new TradeData(tradeId, orderId, symbol, side, quantity, price, timestamp, venue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Trade JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parse MarketData from JSON string
     */
    public static MarketDataData parseMarketData(String json) {
        try {
            String symbol = extractString(json, "symbol");
            long timestamp = extractLong(json, "timestamp");
            long bidPrice = extractLong(json, "bidPrice");
            long bidSize = extractLong(json, "bidSize");
            long askPrice = extractLong(json, "askPrice");
            long askSize = extractLong(json, "askSize");
            long lastPrice = extractLong(json, "lastPrice");
            long lastSize = extractLong(json, "lastSize");
            
            List<PriceLevelData> levels = parsePriceLevels(json);
            
            return new MarketDataData(symbol, timestamp, bidPrice, bidSize, 
                                    askPrice, askSize, lastPrice, lastSize, levels);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse MarketData JSON: " + e.getMessage(), e);
        }
    }
    
    private static List<PriceLevelData> parsePriceLevels(String json) {
        List<PriceLevelData> levels = new ArrayList<>();
        
        // Extract levels array from JSON
        Pattern levelsPattern = Pattern.compile("\"levels\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher levelsMatcher = levelsPattern.matcher(json);
        
        if (levelsMatcher.find()) {
            String levelsContent = levelsMatcher.group(1);
            
            // Split by objects (simple approach for this use case)
            Pattern objectPattern = Pattern.compile("\\{([^}]+)\\}");
            Matcher objectMatcher = objectPattern.matcher(levelsContent);
            
            while (objectMatcher.find()) {
                String levelJson = "{" + objectMatcher.group(1) + "}";
                long price = extractLong(levelJson, "price");
                long size = extractLong(levelJson, "size");
                Side side = Side.valueOf(extractString(levelJson, "side"));
                
                levels.add(new PriceLevelData(price, size, side));
            }
        }
        
        return levels;
    }
    
    private static String extractString(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Field not found: " + fieldName);
    }
    
    private static long extractLong(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + fieldName + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw new IllegalArgumentException("Field not found: " + fieldName);
    }
}
