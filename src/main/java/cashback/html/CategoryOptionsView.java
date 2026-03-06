package cashback.html;

import io.jstach.jstache.JStache;
import java.util.List;

@JStache(template = "{{#categories}}<option>{{.}}</option>{{/categories}}")
public record CategoryOptionsView(List<String> categories) {}
