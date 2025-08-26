package com.github.darioajr.sbe;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * SBE Deserializer for decoding binary messages back to Java objects
 */
public class SbeDeserializer {
    
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final OrderDecoder orderDecoder = new OrderDecoder();
    private final TradeDecoder tradeDecoder = new TradeDecoder();
    private final MarketDataDecoder marketDataDecoder = new MarketDataDecoder();
    
    /**
     * Deserialize binary data to appropriate message type based on template ID
     */
    public Object deserialize(byte[] data) {
        DirectBuffer buffer = new UnsafeBuffer(data);
        
        headerDecoder.wrap(buffer, 0);
        int templateId = headerDecoder.templateId();
        int offset = headerDecoder.encodedLength();
        
        return switch (templateId) {
            case OrderDecoder.TEMPLATE_ID -> deserializeOrder(buffer, offset);
            case TradeDecoder.TEMPLATE_ID -> deserializeTrade(buffer, offset);
            case MarketDataDecoder.TEMPLATE_ID -> deserializeMarketData(buffer, offset);
            default -> throw new IllegalArgumentException("Unknown template ID: " + templateId);
        };
    }
    
    /**
     * Deserialize Order message from binary data
     */
    public OrderData deserializeOrder(byte[] data) {
        DirectBuffer buffer = new UnsafeBuffer(data);
        headerDecoder.wrap(buffer, 0);
        return deserializeOrder(buffer, headerDecoder.encodedLength());
    }
    
    private OrderData deserializeOrder(DirectBuffer buffer, int offset) {
        orderDecoder.wrap(buffer, offset, 
                         headerDecoder.blockLength(), 
                         headerDecoder.version());
        
        // Extract fixed fields
        long orderId = orderDecoder.orderId();
        String symbol = orderDecoder.symbol();
        Side side = orderDecoder.side();
        long quantity = orderDecoder.quantity();
        long price = orderDecoder.price();
        long timestamp = orderDecoder.timestamp();
        BooleanType isActive = orderDecoder.isActive();
        
        // Extract variable length client order ID
        int clientOrderIdLength = orderDecoder.clientOrderIdLength();
        byte[] clientOrderIdBytes = new byte[clientOrderIdLength];
        orderDecoder.getClientOrderId(clientOrderIdBytes, 0, clientOrderIdLength);
        String clientOrderId = new String(clientOrderIdBytes, StandardCharsets.US_ASCII);
        
        return new OrderData(orderId, symbol, side, quantity, price, timestamp, isActive, clientOrderId);
    }
    
    /**
     * Deserialize Trade message from binary data
     */
    public TradeData deserializeTrade(byte[] data) {
        DirectBuffer buffer = new UnsafeBuffer(data);
        headerDecoder.wrap(buffer, 0);
        return deserializeTrade(buffer, headerDecoder.encodedLength());
    }
    
    private TradeData deserializeTrade(DirectBuffer buffer, int offset) {
        tradeDecoder.wrap(buffer, offset, 
                         headerDecoder.blockLength(), 
                         headerDecoder.version());
        
        // Extract fixed fields
        long tradeId = tradeDecoder.tradeId();
        long orderId = tradeDecoder.orderId();
        String symbol = tradeDecoder.symbol();
        Side side = tradeDecoder.side();
        long quantity = tradeDecoder.quantity();
        long price = tradeDecoder.price();
        long timestamp = tradeDecoder.timestamp();
        
        // Extract variable length venue
        int venueLength = tradeDecoder.venueLength();
        byte[] venueBytes = new byte[venueLength];
        tradeDecoder.getVenue(venueBytes, 0, venueLength);
        String venue = new String(venueBytes, StandardCharsets.US_ASCII);
        
        return new TradeData(tradeId, orderId, symbol, side, quantity, price, timestamp, venue);
    }
    
    /**
     * Deserialize MarketData message from binary data
     */
    public MarketDataData deserializeMarketData(byte[] data) {
        DirectBuffer buffer = new UnsafeBuffer(data);
        headerDecoder.wrap(buffer, 0);
        return deserializeMarketData(buffer, headerDecoder.encodedLength());
    }
    
    private MarketDataData deserializeMarketData(DirectBuffer buffer, int offset) {
        marketDataDecoder.wrap(buffer, offset, 
                              headerDecoder.blockLength(), 
                              headerDecoder.version());
        
        // Extract fixed fields
        String symbol = marketDataDecoder.symbol();
        long timestamp = marketDataDecoder.timestamp();
        long bidPrice = marketDataDecoder.bidPrice();
        long bidSize = marketDataDecoder.bidSize();
        long askPrice = marketDataDecoder.askPrice();
        long askSize = marketDataDecoder.askSize();
        long lastPrice = marketDataDecoder.lastPrice();
        long lastSize = marketDataDecoder.lastSize();
        
        // Extract repeating group for price levels
        List<PriceLevelData> levels = new ArrayList<>();
        MarketDataDecoder.LevelsDecoder levelsDecoder = marketDataDecoder.levels();
        while (levelsDecoder.hasNext()) {
            levelsDecoder.next();
            levels.add(new PriceLevelData(
                    levelsDecoder.price(),
                    levelsDecoder.size(),
                    levelsDecoder.side()
            ));
        }
        
        return new MarketDataData(symbol, timestamp, bidPrice, bidSize, 
                                 askPrice, askSize, lastPrice, lastSize, levels);
    }
}
