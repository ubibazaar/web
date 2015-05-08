package services;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;

public abstract class UbibazaarService<Entity> {
  
  // facade
  public static AppService appService = new AppService();
  public static UserService userService = new UserService();
  public static PlatformService platformService = new PlatformService();
  public static InstallationService installationService = new InstallationService();
  public static CategoryService categoryService = new CategoryService();

  private static final String API_URL = "http://ubibazaar.io:8080/ubibazaar-api";

  protected String getResourceUrl() {
    return API_URL + "/resources/" + getResourceName() + "/";
  }

  public Entity get(String id) {
    String url = getResourceUrl() + id;

    @SuppressWarnings("unchecked")
    Promise<Entity> response = WS.url(url).get()
        .map(x -> x.getBody())
        .map(x -> (Entity) new Gson().fromJson(x, getType()));

    return response.get(1, TimeUnit.MINUTES);
  }

  public List<Entity> getList() {
    String url = getResourceUrl();

    @SuppressWarnings("unchecked")
    Promise<List<Entity>> response = WS.url(url).get()
        .map(x -> x.getBody())
        .map(x -> (List<Entity>) new Gson().fromJson(x, getListType()));

    System.out.println(url);

    return response.get(1, TimeUnit.MINUTES);
  }
  
  public List<Entity> query(Map<String, String> params) {
    String url = getResourceUrl();

    // init request
    WSRequestHolder request = WS.url(url + "query");

    // set parameters
    for (Entry<String, String> param : params.entrySet()) {
      request.setQueryParameter(param.getKey(), param.getValue());
    }
    
    @SuppressWarnings("unchecked")
    Promise<List<Entity>> response = request.get()
        .map(x -> x.getBody())
        .map(x -> (List<Entity>) new Gson().fromJson(x, getListType()));

    System.out.println(url);

    return response.get(1, TimeUnit.MINUTES);
  }
  
  public String create(Entity entity) {
    String url = getResourceUrl();
    
    String json = new Gson().toJson(entity);
    
    Promise<WSResponse> re = WS.url(url)
        .setContentType("application/json")
        .post(json);
    
    Promise<List<String>> response = re
        .map(x -> x.getAllHeaders())
        .map(x -> x.get("Location"))
        ;

    return Iterables.getOnlyElement(response.get(1, TimeUnit.MINUTES));
  }

  public abstract Type getType();

  public abstract Type getListType();

  public abstract String getResourceName();

}
