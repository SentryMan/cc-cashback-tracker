package cashback.html;

import io.jstach.jstache.JStache;

@JStache(path = "templates/bestCard.mustache")
public record BestCardView(
    boolean noCards,
    boolean allLimited,
    boolean hasWinner,
    String name,
    String rate,
    String category) {}
