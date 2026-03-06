package cashback.html;

import io.jstach.jstache.JStache;
import java.util.List;

@JStache(
    template =
        "{{#categories}}<button class=\"category-pill px-3 py-1.5 rounded-full text-sm font-semibold bg-slate-100 text-slate-600 transition-colors cursor-pointer\" data-category=\"{{.}}\" onclick=\"selectCategory(this, this.dataset.category)\">{{.}}</button>{{/categories}}")
public record CategoryPillsView(List<String> categories) {}
