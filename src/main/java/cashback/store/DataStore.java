package cashback.store;

import cashback.AppState;
import cashback.model.AppData;
import io.avaje.jsonb.JsonType;
import io.avaje.jsonb.Jsonb;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Singleton
public class DataStore {

  private static final Path DATA_FILE =
      Path.of(System.getProperty("user.home"), ".cashback-tracker", "data.json");
  private static final JsonType<AppData> jsonType = Jsonb.instance().type(AppData.class);

  private final AppState state;

  public DataStore(AppState state) {
    this.state = state;
  }

  public void load() {
    if (Files.exists(DATA_FILE)) {
      try {
        var data = jsonType.fromJson(Files.readString(DATA_FILE));
        state.restore(
            data.cardSeq(), data.txSeq(), data.cards(), data.transactions(), data.categories());
      } catch (Exception e) {
        System.err.println("Failed to load data: " + e.getMessage());
        seedDefaults();
      }
    } else {
      seedDefaults();
    }
  }

  public void save() {
    try {
      Files.createDirectories(DATA_FILE.getParent());
      var data =
          new AppData(
              state.cardSeq(),
              state.txSeq(),
              state.cards(),
              state.transactions(),
              state.categories());
      Files.writeString(DATA_FILE, jsonType.toJson(data));
    } catch (IOException e) {
      System.err.println("Failed to save data: " + e.getMessage());
    }
  }

  private void seedDefaults() {
    state.addCard(
        "Chase Sapphire Preferred",
        "Visa",
        95,
        0,
        Map.of("Dining", 3.0, "Travel", 3.0, "Groceries", 3.0, "Streaming", 3.0, "General", 1.0));
    state.addCard(
        "Citi Double Cash",
        "Mastercard",
        0,
        0,
        Map.of(
            "General",
            2.0,
            "Groceries",
            2.0,
            "Dining",
            2.0,
            "Gas",
            2.0,
            "Online Shopping",
            2.0,
            "Travel",
            2.0,
            "Entertainment",
            2.0,
            "Streaming",
            2.0,
            "Pharmacy",
            2.0));
    state.addCard(
        "Blue Cash Preferred (Amex)",
        "Amex",
        95,
        0,
        Map.of(
            "Groceries",
            6.0,
            "Streaming",
            6.0,
            "Gas",
            3.0,
            "Dining",
            3.0,
            "Online Shopping",
            3.0,
            "General",
            1.0));
    state.addCard(
        "Discover it Cash Back",
        "Discover",
        0,
        0,
        Map.of(
            "Gas", 5.0, "Groceries", 5.0, "Dining", 5.0, "Online Shopping", 5.0, "General", 1.0));
  }
}
