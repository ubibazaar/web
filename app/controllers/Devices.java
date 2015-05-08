package controllers;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Category;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.User;

import play.mvc.Http.Session;
import play.mvc.Result;
import services.UbibazaarService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import views.html.*;

public class Devices extends UbibazaarController {

  public static Result overview() {
    List<Device> devices = UbibazaarService.deviceService.getList(session());

    return ok(device_overview.render(devices));
  }

  public static Result detail(String id) {
    // find the app
    Device device = UbibazaarService.deviceService.get(id, session());
    
    //FIXME load managers and installation
    
    return ok(device_detail.render(device));
  }
//
//  public static Result query() {
//    // find apps according to filter
//    List<App> filtered = UbibazaarService.appService.query(getParams());
//
//    // find entities used for filtering
//    // platform
//    Optional<Platform> platform = getParam("platform")
//        .map(platformId -> UbibazaarService.platformService.get(platformId));
//    // user
//    Optional<User> user = getParam("user")
//        .map(userId -> UbibazaarService.userService.get(userId));
//    // category
//    Optional<Category> category = getParam("category")
//        .map(categoryId -> UbibazaarService.categoryService.get(categoryId));
//
//    return ok(app_store_filtered.render(filtered, platform, user, category));
//  }

}
