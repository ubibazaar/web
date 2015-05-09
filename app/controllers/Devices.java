package controllers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.User;

import play.data.Form;
import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

public class Devices extends UbibazaarController {
  
  public static Form<Device> deviceForm = Form.form(Device.class);
  
  public static Form form = Form.form();

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
  
  public static Result registrationForm() {
    return ok(device_registration.render(UbibazaarService.platformService.getList()));
  }
  
  public static Result register() {
    // load form user data
    Form requestForm = form.bindFromRequest();
    String name = (String)requestForm.data().get("name");
    String platform = (String)requestForm.data().get("platform");

    // instantiate
    Device device = new Device();
    device.setName(name);
    device.setPlatform(UbibazaarService.platformService.get(platform));
    device.setOwner(fetchUserFromSession()); // logged user is the owner

    // register device in api
    String createdDeviceResourceUrl = UbibazaarService.deviceService.create(device, session());

    // find created device id
    Pattern pattern = Pattern.compile("([0-91-f]{32})");
    Matcher matcher = pattern.matcher(createdDeviceResourceUrl);
    if (matcher.find()) {
      String id = matcher.group(1);

      // redirect to profile page
      return redirect("/devices/" + id);
    } else {
      throw new RuntimeException("No user id in response.");
    }
  }

}
