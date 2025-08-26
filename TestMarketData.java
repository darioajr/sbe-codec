import com.example.sbe.*;
import java.util.List;

public class TestMarketData {
    public static void main(String[] args) {
        try {
            SbeSerializer serializer = new SbeSerializer();
            SbeDeserializer deserializer = new SbeDeserializer();
            
            // Create test market data
            MarketDataData marketData = new MarketDataData(
                "AAPL",
                System.currentTimeMillis(),
                15000L, 15100L, 1000L, 900L, 15050L, 500L,
                List.of(
                    new PriceLevelData(15000L, 1000L, Side.BUY),
                    new PriceLevelData(15100L, 900L, Side.SELL)
                )
            );
            
            // Test serialization
            byte[] serialized = serializer.serializeMarketData(marketData);
            System.out.println("Serialized " + serialized.length + " bytes");
            
            // Test deserialization
            MarketDataData deserialized = (MarketDataData) deserializer.deserialize(serialized);
            System.out.println("Deserialized: " + deserialized.symbol());
            System.out.println("Levels: " + deserialized.levels().size());
            
            System.out.println("SUCCESS: MarketData serialization/deserialization works!");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
