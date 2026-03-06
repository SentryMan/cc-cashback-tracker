package cashback.html;

import io.jstach.jstache.JStache;
import java.util.List;

@JStache(
    template =
        "{{#options}}<option value=\"{{id}}\"{{#selected}} selected{{/selected}}>{{name}}</option>{{/options}}")
public record CardOptionsView(List<CardOptionItem> options) {

  public record CardOptionItem(long id, boolean selected, String name) {}
}
