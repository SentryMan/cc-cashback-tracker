package cashback.html;

import io.jstach.jstache.JStache;
import java.util.List;

@JStache(path = "templates/categoryRateInputs.mustache")
public record CategoryRateInputsView(List<CategoryRateInput> inputs) {
  public record CategoryRateInput(String label, String paramName) {}
}
