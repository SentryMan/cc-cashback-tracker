package cashback.html;

import io.jstach.jstache.JStache;
import java.util.List;

@JStache(path = "templates/categoryManage.mustache")
public record CategoryManageView(List<CategoryManageItem> categories) {
  public record CategoryManageItem(String name, String encodedName) {}
}
