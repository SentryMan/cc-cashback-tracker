package cashback.model;

import io.avaje.jsonb.Json;
import java.util.List;

@Json
public record AppData(
    long cardSeq,
    long txSeq,
    List<Card> cards,
    List<Transaction> transactions,
    List<String> categories) {}
