package com.github.darioajr.sbe;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * SBE Serializer for encoding messages to binary format
 */
public class SbeSerializer {
    
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final OrderEncoder orderEncoder = new OrderEncoder();
    private final TradeEncoder tradeEncoder = new TradeEncoder();
    private final MarketDataEncoder marketDataEncoder = new MarketDataEncoder();
    
    /**
     * Serialize an Order message to binary format
     */
    public byte[] serializeOrder(OrderData orderData) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MutableDirectBuffer directBuffer = new UnsafeBuffer(buffer);
        
        int offset = 0;
        
        // Encode header
        headerEncoder.wrap(directBuffer, offset)
                .blockLength(orderEncoder.sbeBlockLength())
                .templateId(orderEncoder.sbeTemplateId())
                .schemaId(orderEncoder.sbeSchemaId())
                .version(orderEncoder.sbeSchemaVersion());
        
        offset += headerEncoder.encodedLength();
        
        // Encode order
        orderEncoder.wrap(directBuffer, offset)
                .orderId(orderData.orderId())
                .symbol(orderData.symbol())
                .side(orderData.side())
                .quantity(orderData.quantity())
                .price(orderData.price())
                .timestamp(orderData.timestamp())
                .isActive(orderData.isActive());
        
        // Encode variable length client order ID
        byte[] clientOrderIdBytes = orderData.clientOrderId().getBytes(StandardCharsets.US_ASCII);
        orderEncoder.putClientOrderId(clientOrderIdBytes, 0, clientOrderIdBytes.length);
        
        int totalLength = MessageHeaderEncoder.ENCODED_LENGTH + orderEncoder.encodedLength();
        byte[] result = new byte[totalLength];
        directBuffer.getBytes(0, result, 0, totalLength);
        
        return result;
    }
    
    /**
     * Serialize a Trade message to binary format
     */
    public byte[] serializeTrade(TradeData tradeData) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MutableDirectBuffer directBuffer = new UnsafeBuffer(buffer);
        
        int offset = 0;
        
        // Encode header
        headerEncoder.wrap(directBuffer, offset)
                .blockLength(tradeEncoder.sbeBlockLength())
                .templateId(tradeEncoder.sbeTemplateId())
                .schemaId(tradeEncoder.sbeSchemaId())
                .version(tradeEncoder.sbeSchemaVersion());
        
        offset += headerEncoder.encodedLength();
        
        // Encode trade
        tradeEncoder.wrap(directBuffer, offset)
                .tradeId(tradeData.tradeId())
                .orderId(tradeData.orderId())
                .symbol(tradeData.symbol())
                .side(tradeData.side())
                .quantity(tradeData.quantity())
                .price(tradeData.price())
                .timestamp(tradeData.timestamp());
        
        // Encode variable length venue
        byte[] venueBytes = tradeData.venue().getBytes(StandardCharsets.US_ASCII);
        tradeEncoder.putVenue(venueBytes, 0, venueBytes.length);
        
        int totalLength = MessageHeaderEncoder.ENCODED_LENGTH + tradeEncoder.encodedLength();
        byte[] result = new byte[totalLength];
        directBuffer.getBytes(0, result, 0, totalLength);
        
        return result;
    }
    
    /**
     * Serialize a MarketData message to binary format
     */
    public byte[] serializeMarketData(MarketDataData marketData) {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        MutableDirectBuffer directBuffer = new UnsafeBuffer(buffer);
        
        int offset = 0;
        
        // Encode header
        headerEncoder.wrap(directBuffer, offset)
                .blockLength(marketDataEncoder.sbeBlockLength())
                .templateId(marketDataEncoder.sbeTemplateId())
                .schemaId(marketDataEncoder.sbeSchemaId())
                .version(marketDataEncoder.sbeSchemaVersion());
        
        offset += headerEncoder.encodedLength();
        
        // Encode market data
        marketDataEncoder.wrap(directBuffer, offset)
                .symbol(marketData.symbol())
                .timestamp(marketData.timestamp())
                .bidPrice(marketData.bidPrice())
                .bidSize(marketData.bidSize())
                .askPrice(marketData.askPrice())
                .askSize(marketData.askSize())
                .lastPrice(marketData.lastPrice())
                .lastSize(marketData.lastSize());
        
        // Encode repeating group for price levels
        MarketDataEncoder.LevelsEncoder levelsEncoder = marketDataEncoder.levelsCount(marketData.levels().size());
        for (PriceLevelData level : marketData.levels()) {
            levelsEncoder.next()
                    .price(level.price())
                    .size(level.size())
                    .side(level.side());
        }
        
        // Calculate total length including repeating groups
        int groupHeaderLength = 4; // 2 bytes blockLength + 2 bytes numInGroup
        int groupDataLength = marketData.levels().size() * 17; // 17 bytes per level (8+8+1)
        int totalLength = MessageHeaderEncoder.ENCODED_LENGTH + marketDataEncoder.encodedLength() + groupHeaderLength + groupDataLength;
        byte[] result = new byte[totalLength];
        directBuffer.getBytes(0, result, 0, totalLength);
        
        return result;
    }
}
