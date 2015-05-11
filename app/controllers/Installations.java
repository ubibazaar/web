package controllers;

import java.util.List;
import java.util.Optional;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Installation;

import com.google.common.collect.ImmutableMap;

import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

public class Installations extends UbibazaarController {

  public static Result install(String id) {
    // secure from accessing without being logged in
    if(fetchUserFromSession().getId() == null) {
      session("afterlogin_redir",request().uri());
      return redirect("/login");
    }
    
    App app = UbibazaarService.appService.get(id);

    ImmutableMap<String, String> platformQuery =
        ImmutableMap.of("platform", app.getPlatform().getId());
    List<Device> devices = UbibazaarService.deviceService.query(platformQuery, session());

    return ok(device_selection.render(app, devices));
  }

  public static Result installTo(String appId, String deviceId) {
    // secure from accessing without being logged in
    if(fetchUserFromSession().getId() == null) {
      session("afterlogin_redir","/install/" + appId);
      return redirect("/login");
    }
    
    App app = UbibazaarService.appService.get(appId);
    Device device = UbibazaarService.deviceService.get(deviceId, session());

    Installation installation = new Installation();
    installation.setApp(app);
    installation.setDevice(device);

    String url = UbibazaarService.installationService.create(installation, session());
    Optional<String> id = extractId(url);

    if (id.isPresent()) {
      // FIXME show info about successfull installation
      return redirect("/devices/" + device.getId());
    } else {
      //FIXME show error message
      return badRequest();
    }
  }
  
  public static Result uninstall(String installationId) {
    // secure from accessing without being logged in
    if(fetchUserFromSession().getId() == null) {
      session("afterlogin_redir",request().uri());
      return redirect("/login");
    }
    
    Installation inst = UbibazaarService.installationService.get(installationId, session());
    
    if(UbibazaarService.installationService.delete(installationId, session())) {
      return redirect("/devices/" + inst.getDevice().getId());
    } else {
      //FIXME show error message
      return badRequest();
    }
  }

}
