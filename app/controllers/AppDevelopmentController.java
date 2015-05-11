package controllers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.ManagerType;
import org.ubicollab.ubibazaar.core.Platform;
import org.ubicollab.ubibazaar.core.Category;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import play.data.Form;
import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

public class AppDevelopmentController extends UbibazaarController {

  public static Form form = Form.form();
  
  public static Result step1() {
    // secure from accessing without being logged in
    if(fetchUserFromSession().getId() == null) {
      session("afterlogin_redir",request().uri());
      return redirect("/login");
    }
    
    List<Platform> platforms = UbibazaarService.platformService.getList();
    Category categories = UbibazaarService.categoryService.get("");
    return ok(app_registration_step1.render(platforms, categories));
  }
  
  public static Result step2() {
    // secure from accessing without being logged in
    if(fetchUserFromSession().getId() == null) {
      session("afterlogin_redir","newapp_1");
      return redirect("/login");
    }
    
    Form requestForm = form.bindFromRequest();
    String name = (String) requestForm.data().get("name");
    String platform = (String) requestForm.data().get("platform");
    String description = (String) requestForm.data().get("description");
    
    List<ManagerType> managerTypes = UbibazaarService.managerTypeService.query(ImmutableMap.of("platform", platform));
    
    Table<ManagerType, String, String> propertyTable = HashBasedTable.create();
    
    for (ManagerType managerType : managerTypes) {
      Map<String, String> props = new Gson().fromJson(managerType.getInstallationMethod().getProperties(), new TypeToken<Map<String, String>>() {}.getType());
      
      for (Entry<String, String> property : props.entrySet()) {
        propertyTable.put(managerType, property.getKey(), property.getValue());
      }
    }
    
    return ok(app_registration_step2.render(name, platform, description, propertyTable));
  }
  
  public static Result add() {
    // secure from accessing without being logged in
    if(fetchUserFromSession().getId() == null) {
      session("afterlogin_redir","newapp_1");
      return redirect("/login");
    }
    
    Form requestForm = form.bindFromRequest();
    String name = (String) requestForm.data().get("name");
    String platform = (String) requestForm.data().get("platform");
    String description = (String) requestForm.data().get("description");

    // find properties
    Map<String, String> receivedProperties = findReceivedProperties(requestForm, platform);
    
    // instantiate
    App app = new App();
    app.setName(name);
    app.setPlatform(UbibazaarService.platformService.get(platform));
    app.setAuthor(fetchUserFromSession()); // logged user is the owner
    app.setDescription(description);
    app.setProperties(new Gson().toJson(receivedProperties));
    
    //FIXME categories
    
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

  private static Map<String, String> findReceivedProperties(Form requestForm, String platform) {
    List<ManagerType> managerTypes = UbibazaarService.managerTypeService.query(ImmutableMap.of("platform", platform));
    
    Map<String, String> sentProperties = Maps.newHashMap();
    
    for (ManagerType managerType : managerTypes) {
      Map<String, String> knownProperties = new Gson().fromJson(managerType.getInstallationMethod().getProperties(), new TypeToken<Map<String, String>>() {}.getType());
      
      for (Entry<String, String> knownProperty : knownProperties.entrySet()) {
        String propertyName = knownProperty.getKey();
        
        if(requestForm.data().containsKey(propertyName)) {
          sentProperties.put(propertyName, (String)requestForm.data().get(propertyName));
        }
      }
    }
    return sentProperties;
  }

}
