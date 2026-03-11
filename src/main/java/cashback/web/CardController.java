package cashback.web;

import cashback.AppState;
import cashback.html.HtmlRenderer;
import cashback.store.DataStore;
import io.avaje.http.api.Controller;
import io.avaje.http.api.Default;
import io.avaje.http.api.Delete;
import io.avaje.http.api.Form;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
import io.avaje.http.api.Produces;
import io.avaje.jex.http.Context;
import java.util.Map;

@Controller("/cards")
public class CardController {

  private final AppState state;
  private final DataStore dataStore;
  private final HtmlRenderer renderer;

  public CardController(AppState state, DataStore dataStore, HtmlRenderer renderer) {
    this.state = state;
    this.dataStore = dataStore;
    this.renderer = renderer;
  }

  @Get
  void list(Context ctx) {
    ctx.html(renderer.cardsListHtml());
  }

  @Post
  @Form
  void add(
      Context ctx,
      String name,
      String network,
      @Default("0") double fee,
      @Default("0") double rewardLimit) {
    if (name == null || name.isBlank()) {
      ctx.status(400);
      return;
    }

    var rates = Map.of(AppState.GENERAL_CATEGORY, 1.0);

    var card = state.addCard(name, network != null ? network : "Other", fee, rewardLimit, rates);
    dataStore.save();
    ctx.html(renderer.cardHtml(card));
  }

  @Post("/{id}/rates")
  void addRate(Context ctx, long id) {
    String category = ctx.formParam("category");
    String rateStr = ctx.formParam("rate");
    if (category == null || rateStr == null || rateStr.isBlank()) {
      ctx.status(400);
      return;
    }
    double rate;
    try {
      rate = Double.parseDouble(rateStr);
    } catch (NumberFormatException e) {
      ctx.status(400);
      return;
    }
    state.addCategoryRate(id, category, rate);
    dataStore.save();
    state.findCard(id).ifPresentOrElse(c -> ctx.html(renderer.cardHtml(c)), () -> ctx.status(404));
  }

  @Delete("/{id}/rates/{category}")
  @Produces(statusCode = 200)
  void removeRate(Context ctx, long id, String category) {
    state.removeCategoryRate(id, category);
    dataStore.save();
    state.findCard(id).ifPresentOrElse(c -> ctx.html(renderer.cardHtml(c)), () -> ctx.status(404));
  }

  @Delete("/{id}/earnings")
  @Produces(statusCode = 200)
  void resetEarnings(Context ctx, long id) {
    state.resetSpend(id);
    dataStore.save();
    var card = state.findCard(id);
    if (card.isEmpty()) {
      ctx.status(404);
      return;
    }
    ctx.html(renderer.cardHtml(card.get()) + renderer.txListOob());
  }

  @Delete("/{id}")
  @Produces(statusCode = 200)
  void delete(Context ctx, long id) {
    state.removeCard(id);
    dataStore.save();
    ctx.html(renderer.txListOob());
  }
}
