package cashback;

import cashback.store.DataStore;
import io.avaje.inject.BeanScope;
import io.avaje.jex.Jex;
import io.avaje.jex.staticcontent.StaticContent;
import io.avaje.webview.Webview;

public class MainCash {

  void main(String[] args) {
    var scope = BeanScope.builder().build();

    // Load persisted data (or seed defaults) before the server starts
    scope.get(DataStore.class).load();

    var jex =
        Jex.create()
            .plugin(
                StaticContent.ofClassPath("/static/index.html")
                    .resourceLoader(MainCash.class)
                    .route("/")
                    .build());

    var server = jex.configureWith(scope).port(0).start();

    try (var webview =
        Webview.builder()
            .title("Cashback Tracker")
            .navigate("http://localhost:" + server.port())
            .enableDeveloperTools(true)
            .build()) {
      webview.run();
    } finally {
      server.shutdown();
    }
  }
}
