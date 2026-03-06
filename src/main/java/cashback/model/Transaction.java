package cashback.model;

import io.avaje.jsonb.Json;

@Json
public record Transaction(
    long id, long cardId, String category, double amount, double cashback, String date) {}
