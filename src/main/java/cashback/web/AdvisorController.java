package cashback.web;

import cashback.html.CardOptionsView;
import cashback.html.HtmlRenderer;
import io.avaje.http.api.Controller;
import io.avaje.http.api.Get;
import io.avaje.jex.http.Context;

@Controller
public class AdvisorController {

  private final HtmlRenderer renderer;

  public AdvisorController(HtmlRenderer renderer) {
    this.renderer = renderer;
  }

  @Get("/best/{category}")
  void best(Context ctx, String category) {
    ctx.html(renderer.bestCardHtml(category));
  }

  @Get("/card-options")
  CardOptionsView cardOptions(Context ctx) {
    return renderer.cardOptionsHtml(0);
  }
}
