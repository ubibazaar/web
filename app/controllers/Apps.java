package controllers;

import java.util.Collection;
import java.util.List;

import org.ubicollab.ubibazaar.core.App;

import play.*;
import play.mvc.*;
import services.AppService;
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
    //FIXME filtering on author, platform and categories..
    List<App> similarApps = appService.getList();
    
    return ok(app_detail.render(app, similarApps));
  }

}
