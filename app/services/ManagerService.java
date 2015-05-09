package services;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Manager;

import play.libs.ws.WS;
import play.mvc.Http.Session;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class ManagerService extends UbibazaarService<Manager> {

  @Override
  public String getResourceName() {
    return "managers";
  }

  @SuppressWarnings("serial")
  @Override
  public Type getType() {
    return new TypeToken<Manager>() {}.getType();
  }

  @SuppressWarnings("serial")
  @Override
  public Type getListType() {
    return new TypeToken<List<Manager>>() {}.getType();
  }
  
  @Override
  public Boolean authNeeded() {
    return true;
  }
  
  public boolean linkDeviceToManager(Manager manager, Device device, Session session) {
    String url = getResourceUrl().replace(getResourceName(), "pairings") + manager.getId();
    
    System.out.println(url);
    
    String json = new Gson().toJson(device);
    
    return WS.url(url)
        .setAuth(session.get("username"), session.get("password"))
        .setContentType("application/json")
        .post(json)
        .map(response -> response.getStatus())
        .map(responseCode -> responseCode == 200)
        .get(1, TimeUnit.MINUTES);
  }

}
