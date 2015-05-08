package controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Category;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.User;

import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

import com.google.common.collect.Maps;

public class Apps extends UbibazaarController {
  
  public static Result index() {
    List<App> apps = UbibazaarService.appService.getList();

    return ok(app_store.render(apps));
  }

  public static Result detail(String id) {
    // find the app
    App app = UbibazaarService.appService.get(id);

    // find similar apps
    // FIXME filtering on author, platform and categories..
    List<App> similarApps = UbibazaarService.appService.getList();

    return ok(app_detail.render(app, similarApps));
  }

  public static Result query() {
    // propagate params
    Map<String, String> params = Maps.newHashMap();
    request().queryString().entrySet()
      .forEach(queryParam-> params.put(queryParam.getKey(), queryParam.getValue()[0]));
    
    // find apps according to filter
    List<App> filtered = UbibazaarService.appService.query(params);

    // find entities used for filtering
    // platform
    Optional<Platform> platform = getParam("platform")
        .map(platformId -> UbibazaarService.platformService.get(platformId));
    // user
    Optional<User> user = getParam("user")
        .map(userId -> UbibazaarService.userService.get(userId));
    // category
    Optional<Category> category = Optional.empty();

    return ok(app_store_filtered.render(filtered, platform, user, category));
  }

}
