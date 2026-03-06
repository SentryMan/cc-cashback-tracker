package cashback;

import cashback.model.Card;
import cashback.model.Transaction;
import jakarta.inject.Singleton;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class AppState {

  /** The catch-all category whose rate applies to any category not otherwise specified. */
  public static final String GENERAL_CATEGORY = "General";

  public static final List<String> DEFAULT_CATEGORIES =
      List.of(
          "Groceries",
          "Dining",
          "EV Charging",
          "Digital Wallet",
          "Online Shopping",
          "Entertainment",
          "Streaming",
          "Pharmacy",
          GENERAL_CATEGORY);

  /**
   * Returns the cashback rate for {@code category} on {@code card}. Falls back to {@link
   * #GENERAL_CATEGORY} if no specific rate is set, then to 0.
   */
  public static double rateFor(Card card, String category) {
    var rates = card.rates();
    if (rates.containsKey(category)) return rates.get(category);
    return rates.getOrDefault(GENERAL_CATEGORY, 0.0);
  }

  private final AtomicLong cardSeq = new AtomicLong(1);
  private final AtomicLong txSeq = new AtomicLong(1);
  private final List<Card> cards = new ArrayList<>();
  private final List<Transaction> transactions = new ArrayList<>();
  private final List<String> categories = new ArrayList<>(DEFAULT_CATEGORIES);

  public List<Card> cards() {
    return List.copyOf(cards);
  }

  public List<Transaction> transactions() {
    return List.copyOf(transactions);
  }

  public List<String> categories() {
    return List.copyOf(categories);
  }

  public long cardSeq() {
    return cardSeq.get();
  }

  public long txSeq() {
    return txSeq.get();
  }

  public Card addCard(
      String name, String network, double fee, double rewardLimit, Map<String, Double> rates) {
    var card =
        new Card(
            cardSeq.getAndIncrement(), name, network, fee, rewardLimit, new LinkedHashMap<>(rates));
    cards.add(card);
    return card;
  }

  public void removeCard(long id) {
    cards.removeIf(c -> c.id() == id);
    transactions.removeIf(t -> t.cardId() == id);
  }

  public void resetSpend(long cardId) {
    transactions.removeIf(t -> t.cardId() == cardId);
  }

  public void addTransaction(Transaction tx) {
    transactions.add(tx);
  }

  public void removeTransaction(long id) {
    transactions.removeIf(t -> t.id() == id);
  }

  public long nextTxId() {
    return txSeq.getAndIncrement();
  }

  public Optional<Card> findCard(long id) {
    return cards.stream().filter(c -> c.id() == id).findFirst();
  }

  public double spentByCard(long cardId) {
    return transactions.stream()
        .filter(t -> t.cardId() == cardId)
        .mapToDouble(Transaction::amount)
        .sum();
  }

  public boolean addCategoryRate(long cardId, String category, double rate) {
    return findCard(cardId).map(c -> c.rates().put(category, rate) != null || true).orElse(false);
  }

  public boolean removeCategoryRate(long cardId, String category) {
    return findCard(cardId).map(c -> c.rates().remove(category) != null).orElse(false);
  }

  public void addCategory(String name) {
    if (!categories.contains(name)) categories.add(name);
  }

  public void removeCategory(String name) {
    categories.remove(name);
  }

  /** Called by DataStore to restore persisted state. */
  public void restore(
      long cardSeq,
      long txSeq,
      List<Card> cards,
      List<Transaction> transactions,
      List<String> savedCategories) {
    this.cardSeq.set(cardSeq);
    this.txSeq.set(txSeq);
    this.cards.addAll(cards);
    this.transactions.addAll(transactions);
    if (savedCategories != null && !savedCategories.isEmpty()) {
      this.categories.clear();
      this.categories.addAll(savedCategories);
    }
  }
}
