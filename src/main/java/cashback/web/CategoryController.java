package cashback.web;

import cashback.AppState;
import cashback.html.CategoryManageView;
import cashback.html.CategoryOptionsView;
import cashback.html.CategoryPillsView;
import cashback.html.CategoryRateInputsView;
import cashback.html.HtmlRenderer;
import cashback.store.DataStore;
import io.avaje.http.api.Controller;
import io.avaje.http.api.Delete;
import io.avaje.http.api.Get;
import io.avaje.http.api.Post;
import io.avaje.http.api.Produces;
import io.avaje.jex.http.Context;

@Controller("/categories")
public class CategoryController {

  private final AppState state;
  private final DataStore dataStore;
  private final HtmlRenderer renderer;

  public CategoryController(AppState state, DataStore dataStore, HtmlRenderer renderer) {
    this.state = state;
    this.dataStore = dataStore;
    this.renderer = renderer;
  }

  @Get("/pills")
  CategoryPillsView pills() {
    return renderer.categoryPillsHtml();
  }

  @Get("/options")
  CategoryOptionsView options() {
    return renderer.categoryOptionsHtml();
  }

  @Get("/rate-inputs")
  CategoryRateInputsView rateInputs() {
    return renderer.categoryRateInputsHtml();
  }

  @Get("/manage")
  CategoryManageView manage() {
    return renderer.categoryManageHtml();
  }

  @Post
  void add(Context ctx) {
    String name = ctx.formParam("name");
    if (name == null || name.isBlank()) {
      ctx.status(400);
      return;
    }
    state.addCategory(name.trim());
    dataStore.save();
    ctx.html(renderer.categoryManageInnerHtml() + renderer.categoryChangeOob());
  }

  @Delete("/{name}")
  @Produces(statusCode = 200)
  void delete(Context ctx, String name) {
    state.removeCategory(name);
    dataStore.save();

    ctx.html(renderer.categoryManageInnerHtml() + renderer.categoryChangeOob());
  }
}
