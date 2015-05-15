package controllers;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Installation;
import org.ubicollab.ubibazaar.core.Manager;
import org.ubicollab.ubibazaar.core.Platform;
import com.google.common.collect.ImmutableMap;

import play.data.Form;
import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

public class Devices extends UbibazaarController {
  
  public static Form form = Form.form();

  public static Result overview() {
    // secure from accessing without being logged in
    if(fetchUserFromSession().getId() == null) {
      session("afterlogin_redir",request().uri());
      return redirect("/login");
    }
    
    List<Device> devices = UbibazaarService.deviceService.getList(session());

    return ok(device_overview.render(devices));
  }

  public static Result detail(String id) {
    // secure from accessing without being logged in
    if(fetchUserFromSession().getId() == null) {
      session("afterlogin_redir",request().uri());
      return redirect("/login");
    }
    
    // find the app
    Device device = UbibazaarService.deviceService.get(id, session());

    // query for managers and installations on this device
    ImmutableMap<String, String> deviceQuery = ImmutableMap.of("device", device.getId());
    List<Manager> managers = UbibazaarService.managerService.query(deviceQuery, session());
    List<Installation> installations = UbibazaarService.installationService.query(deviceQuery, session());
    
    return ok(device_detail.render(device, managers, installations));
  }

  public static Result query() {
    // secure from accessing without being logged in
    if(fetchUserFromSession().getId() == null) {
      session("afterlogin_redir",request().uri());
      return redirect("/login");
    }
    
    // find devices according to filter
    List<Device> filtered = UbibazaarService.deviceService.query(getParams(), session());

    // find entities used for filtering
    // platform
    Optional<Platform> platform = getParam("platform")
        .map(platformId -> UbibazaarService.platformService.get(platformId));

    return ok(device_overview_filtered.render(platform, filtered));
  }
  
  public static Result registrationForm() {
    // secure from accessing without being logged in
    if(fetchUserFromSession().getId() == null) {
      session("afterlogin_redir",request().uri());
      return redirect("/login");
    }
    
    return ok(device_registration.render(UbibazaarService.platformService.getList(), Optional.<String>empty()));
  }
  
  public static Result register() {
    try {
      // secure from accessing without being logged in
      if(fetchUserFromSession().getId() == null) {
        session("afterlogin_redir","/devices/add");
        return redirect("/login");
      }
      
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
        throw new RuntimeException("No device id in response.");
      }
    } catch(IllegalArgumentException e) {
      return ok(device_registration.render(UbibazaarService.platformService.getList(), Optional.of(e.getMessage())));
    }
  }

}
