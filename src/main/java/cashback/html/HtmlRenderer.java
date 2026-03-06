package cashback.html;

import cashback.AppState;
import cashback.html.CardOptionsView.CardOptionItem;
import cashback.html.CardView.RateBadge;
import cashback.html.CategoryManageView.CategoryManageItem;
import cashback.html.CategoryRateInputsView.CategoryRateInput;
import cashback.model.Card;
import cashback.model.Transaction;
import io.jstach.jstachio.JStachio;
import jakarta.inject.Singleton;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Singleton
public class HtmlRenderer {

  private final AppState state;

  public HtmlRenderer(AppState state) {
    this.state = state;
  }

  public String cardHtml(Card c) {
    boolean hasAnnualFee = c.annualFee() > 0;
    boolean hasLimit = c.rewardLimit() > 0;
    double spent = (hasLimit || hasAnnualFee) ? state.spentByCard(c.id()) : 0;
    boolean limitReached = hasLimit && spent >= c.rewardLimit();

    List<RateBadge> rates =
        c.rates().entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .map(
                e ->
                    new RateBadge(
                        badgeColor(e.getValue()),
                        e.getKey(),
                        "%.0f%%".formatted(e.getValue()),
                        c.id(),
                        URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8).replace("+", "%20")))
            .toList();

    String pct = hasLimit ? "%.1f".formatted(Math.min(100.0, spent / c.rewardLimit() * 100)) : "0";
    String barColor = limitReached ? "bg-red-500" : "bg-green-500";
    String annualFee = c.annualFee() == 0 ? "Free" : "$" + (int) c.annualFee();

    return JStachio.render(
        new CardView(
            c.id(),
            c.name(),
            c.network(),
            annualFee,
            rates,
            hasAnnualFee,
            hasLimit,
            limitReached,
            "$%.2f".formatted(spent),
            "$%.2f".formatted(c.rewardLimit()),
            pct,
            barColor,
            state.categories()));
  }

  public String cardsListHtml() {
    var sb = new StringBuilder();
    state.cards().forEach(c -> sb.append(cardHtml(c)));
    return sb.toString();
  }

  public String txHtml(Transaction tx) {
    return state
        .findCard(tx.cardId())
        .map(
            c ->
                JStachio.render(
                    new TxView(
                        tx.date(),
                        c.name(),
                        tx.category(),
                        "$%.2f".formatted(tx.amount()),
                        "+$%.2f".formatted(tx.cashback()),
                        tx.id())))
        .orElse("");
  }

  public String bestCardHtml(String category) {
    if (state.cards().isEmpty())
      return JStachio.render(new BestCardView(true, false, false, "", "", ""));

    var available =
        state.cards().stream()
            .filter(c -> c.rewardLimit() <= 0 || state.spentByCard(c.id()) < c.rewardLimit())
            .toList();

    if (available.isEmpty())
      return JStachio.render(new BestCardView(false, true, false, "", "", ""));

    return available.stream()
        .max(Comparator.comparingDouble(c -> AppState.rateFor(c, category)))
        .map(
            c -> {
              double rate = AppState.rateFor(c, category);
              return JStachio.render(
                  new BestCardView(
                      false, false, true, c.name(), "%.1f%%".formatted(rate), category));
            })
        .orElse("");
  }

  // View-model builders (returned directly from controller GET endpoints)

  public CardOptionsView cardOptionsHtml(long selectedId) {
    List<CardOptionItem> options =
        state.cards().stream()
            .map(c -> new CardOptionItem(c.id(), c.id() == selectedId, c.name()))
            .toList();
    return new CardOptionsView(options);
  }

  public CategoryPillsView categoryPillsHtml() {
    return new CategoryPillsView(state.categories());
  }

  public CategoryOptionsView categoryOptionsHtml() {
    return new CategoryOptionsView(state.categories());
  }

  public CategoryRateInputsView categoryRateInputsHtml() {
    List<CategoryRateInput> inputs =
        state.categories().stream()
            .map(cat -> new CategoryRateInput(cat, "rate_" + cat.replace(" ", "_").toLowerCase()))
            .toList();
    return new CategoryRateInputsView(inputs);
  }

  public CategoryManageView categoryManageHtml() {
    List<CategoryManageItem> items =
        state.categories().stream()
            .map(
                cat ->
                    new CategoryManageItem(
                        cat, URLEncoder.encode(cat, StandardCharsets.UTF_8).replace("+", "%20")))
            .toList();
    return new CategoryManageView(items);
  }

  // String-returning methods for OOB fragments and composite responses

  public String categoryManageInnerHtml() {
    return JStachio.render(categoryManageHtml());
  }

  /** OOB tbody replacement used when bulk-deleting transactions (reset spend, remove card). */
  public String txListOob() {
    var sb = new StringBuilder();
    state.transactions().stream()
        .sorted(Comparator.comparingLong(Transaction::id).reversed())
        .forEach(t -> sb.append(txHtml(t)));
    return "<tbody id=\"tx-body\" hx-swap-oob=\"innerHTML\">" + sb + "</tbody>";
  }

  /** OOB updates triggered when the category list changes. */
  public String categoryChangeOob() {
    return """
        <div id="category-pills" hx-swap-oob="innerHTML">%s</div>
        <div id="best-card-result" hx-swap-oob="innerHTML"></div>
        """
        .formatted(JStachio.render(categoryPillsHtml()));
  }

  private static String badgeColor(double pct) {
    if (pct >= 5) return "bg-green-100 text-green-700";
    if (pct >= 3) return "bg-blue-100 text-blue-700";
    if (pct >= 2) return "bg-indigo-100 text-indigo-700";
    return "bg-slate-100 text-slate-600";
  }
}
