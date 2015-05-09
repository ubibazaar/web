package controllers;

import java.util.List;

import org.ubicollab.ubibazaar.core.App;

import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

public class Main extends UbibazaarController {
  
  public static Result landing() {
    List<App> apps = UbibazaarService.appService.getList();

    return ok(landing.render(apps));
  }

}
