package cashback.web;

import cashback.AppState;
import cashback.html.HtmlRenderer;
import cashback.model.Transaction;
import cashback.store.DataStore;
import io.avaje.http.api.Controller;
import io.avaje.http.api.Delete;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
import io.avaje.http.api.Produces;
import io.avaje.jex.http.Context;
import java.time.LocalDate;
import java.util.Comparator;

@Controller("/transactions")
public class TransactionController {

  private final AppState state;
  private final DataStore dataStore;
  private final HtmlRenderer renderer;

  public TransactionController(AppState state, DataStore dataStore, HtmlRenderer renderer) {
    this.state = state;
    this.dataStore = dataStore;
    this.renderer = renderer;
  }

  @Get
  void list(Context ctx) {
    var sb = new StringBuilder();
    state.transactions().stream()
        .sorted(Comparator.comparingLong(Transaction::id).reversed())
        .forEach(t -> sb.append(renderer.txHtml(t)));
    ctx.html(sb.toString());
  }

  @Post
  void add(Context ctx) {
    long cardId;
    double amount;
    try {
      cardId = Long.parseLong(ctx.formParam("card_id"));
      amount = Double.parseDouble(ctx.formParam("amount"));
    } catch (Exception e) {
      ctx.status(400);
      return;
    }

    String category = ctx.formParam("category");
    if (category == null) category = AppState.GENERAL_CATEGORY;

    var card = state.findCard(cardId);
    if (card.isEmpty()) {
      ctx.status(400);
      return;
    }

    double rate = AppState.rateFor(card.get(), category);
    double cashback = amount * rate / 100.0;
    var tx =
        new Transaction(
            state.nextTxId(), cardId, category, amount, cashback, LocalDate.now().toString());
    state.addTransaction(tx);
    dataStore.save();

    ctx.html(renderer.txHtml(tx));
  }

  @Delete("/{id}")
  @Produces(statusCode = 200)
  void delete(Context ctx, long id) {
    state.removeTransaction(id);
    dataStore.save();
    ctx.html("<tr></tr>");
  }
}
