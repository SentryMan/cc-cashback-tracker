package cashback.model;

import io.avaje.jsonb.Json;
import java.util.Map;

@Json
public record Card(
    long id,
    String name,
    String network,
    double annualFee,
    double rewardLimit,
    Map<String, Double> rates) {}
