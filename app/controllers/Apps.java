package controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Category;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.User;

import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class Apps extends UbibazaarController {
  
  public static Result index() {
    List<App> apps = UbibazaarService.appService.getList();

    return ok(app_store.render(apps));
  }

  public static Result detail(String id) {
    // find the app
    App app = UbibazaarService.appService.get(id);

    // find similar apps - same author, same platform or same category
    List<App> sameAuthorApps = UbibazaarService.appService.query(ImmutableMap.of("user", app.getAuthor().getId()));
    List<App> samePlatformApps = UbibazaarService.appService.query(ImmutableMap.of("user", app.getPlatform().getId()));
    //FIXME same category apps
    
    Set<App> similarApps = Sets.newHashSet();
    similarApps.addAll(samePlatformApps);
    similarApps.addAll(sameAuthorApps);
    similarApps.remove(app); // do not show this app as similar to itself

    return ok(app_detail.render(app, similarApps));
  }

  public static Result query() {
    // find apps according to filter
    List<App> filtered = UbibazaarService.appService.query(getParams());

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
