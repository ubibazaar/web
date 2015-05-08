package controllers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Category;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.User;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import play.*;
import play.mvc.*;
import services.AppService;
import services.PlatformService;
import services.UserService;
import views.html.*;

public class Apps extends Controller {

  public static Result index() {
    List<App> apps = new AppService().getList();

    return ok(app_store.render(apps));
  }

  public static Result detail(String id) {
    AppService appService = new AppService();

    // find the app
    App app = appService.get(id);

    // find similar apps
    // FIXME filtering on author, platform and categories..
    List<App> similarApps = appService.getList();

    return ok(app_detail.render(app, similarApps));
  }

  public static Result query() {
    AppService appService = new AppService();

    // propagate params
    Map<String, String> params = Maps.newHashMap();
    request().queryString().entrySet()
      .forEach(queryParam-> params.put(queryParam.getKey(), queryParam.getValue()[0]));
    
    // find apps according to filter
    List<App> filtered = appService.query(params);

    // find entities used for filtering
    // platform
    Optional<Platform> platform = getParam("platform")
        .map(platformId -> new PlatformService().get(platformId));
    // user
    Optional<User> user = getParam("user")
        .map(userId -> new UserService().get(userId));
    // category
    Optional<Category> category = Optional.empty();

    return ok(app_store_filtered.render(filtered, platform, user, category));
  }

  private static Optional<String> getParam(String paramName) {
    String[] queryString = request().queryString().get(paramName);

    if (queryString != null && queryString.length > 0) {
      return Optional.of(queryString[0]);
    } else {
      return Optional.empty();
    }
  }

}
