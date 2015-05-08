package controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Category;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.User;

import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

public class Apps extends UbibazaarController {
  
  public static Result index() {
    List<App> apps = UbibazaarService.appService.getList();

    return ok(app_store.render(apps));
  }

  public static Result detail(String id) {
    // find the app
    App app = UbibazaarService.appService.get(id);
    
    System.out.println(app.getCategory().isEmpty());

    // find similar apps - same author, same platform or same category
    List<App> sameAuthorApps = UbibazaarService.appService.query(ImmutableMap.of("user", app.getAuthor().getId()));
    List<App> samePlatformApps = UbibazaarService.appService.query(ImmutableMap.of("user", app.getPlatform().getId()));
    List<App> sameCategoryApps = (List<App>) app.getCategory().stream()
      .map(category-> category.getId())
      .map(categoryId-> ImmutableMap.of("category", categoryId))
      .flatMap(categoryParam -> UbibazaarService.appService.query(categoryParam).stream())
      .collect(Collectors.toList());
    
    Set<App> similarApps = Sets.newHashSet();
    similarApps.addAll(samePlatformApps);
    similarApps.addAll(sameAuthorApps);
    similarApps.addAll(sameCategoryApps);
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
    Optional<Category> category = getParam("category")
        .map(categoryId -> UbibazaarService.categoryService.get(categoryId));

    return ok(app_store_filtered.render(filtered, platform, user, category));
  }
  
  public static void main(String[] args) {
    String s = "{\"id\": \"f47754d0c689443ebc1bbf0cd2c8bd86\",\"name\": \"energy-monitoring\",\"parent\": {\"id\": \"86b90e8171ce49c8a9a487b31c818a7a\",\"name\": \"smart-home\",\"parent\": {\"id\": \"33a9a87811a04743966cddb5e82e1fb7\",\"name\": \"internet-of-things\",\"parent\": {\"id\": \"4220f24cd59f4ef6b2b07f74a1e264bd\",\"name\": \"root category\"}}}}";
    Category cat = new Gson().fromJson(s, Category.class);
    
    System.out.println(cat.getName());
  }

}
