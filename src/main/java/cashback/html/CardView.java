package cashback.html;

import io.jstach.jstache.JStache;
import java.util.List;

@JStache(path = "templates/card.mustache")
public record CardView(
    long id,
    String name,
    String network,
    String annualFee,
    List<RateBadge> rates,
    boolean hasAnnualFee,
    boolean hasLimit,
    boolean limitReached,
    String spentFormatted,
    String rewardLimitFormatted,
    String pct,
    String barColor,
    List<String> categories) {

  public record RateBadge(
      String cssClass, String category, String rate, long cardId, String encodedCategory) {}
}
