package controllers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.Category;

import play.data.Form;
import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

public class AppDevelopmentController extends UbibazaarController {

  public static Form form = Form.form();
  
  public static Result form() {
    List<Platform> platforms = UbibazaarService.platformService.getList();
    Category categories = UbibazaarService.categoryService.get("");
    return ok(app_registration.render(platforms, categories));
  }

  public static Result add() {
    // load form user data
    Form requestForm = form.bindFromRequest();
    String name = (String) requestForm.data().get("name");
    String platform = (String) requestForm.data().get("platform");
    String description = (String) requestForm.data().get("description");

    // instantiate
    App app = new App();
    app.setName(name);
    app.setPlatform(UbibazaarService.platformService.get(platform));
    app.setAuthor(fetchUserFromSession()); // logged user is the owner
    app.setDescription(description);
    
    //FIXME set categories and properties
    //app.setProperties(null);
    //app.setCategory(null);
    
    // register app in api
    String createdDeviceResourceUrl = UbibazaarService.appService.create(app, session());

    // find created app id
    Pattern pattern = Pattern.compile("([0-91-f]{32})");
    Matcher matcher = pattern.matcher(createdDeviceResourceUrl);
    if (matcher.find()) {
      String id = matcher.group(1);

      // redirect to profile page
      return redirect("/apps/" + id);
    } else {
      throw new RuntimeException("No app id in response.");
    }
  }

}
