package cashback.html;

import io.jstach.jstache.JStache;

@JStache(path = "templates/tx.mustache")
public record TxView(
    String date, String cardName, String category, String amount, String cashback, long id) {}
