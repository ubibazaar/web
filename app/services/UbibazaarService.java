package services;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.Http.Session;
import play.mvc.Http.Status;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;

public abstract class UbibazaarService<Entity> {
  
  // facade
  public static AppService appService = new AppService();
  public static UserService userService = new UserService();
  public static PlatformService platformService = new PlatformService();
  public static InstallationService installationService = new InstallationService();
  public static CategoryService categoryService = new CategoryService();
  public static DeviceService deviceService = new DeviceService();
  public static ManagerService managerService = new ManagerService();
  public static ManagerTypeService managerTypeService = new ManagerTypeService();

  private static final String API_URL = "http://ubibazaar.com:8080/ubibazaar-api";

  protected String getResourceUrl() {
    return API_URL + "/resources/" + getResourceName() + "/";
  }
  
  public Entity get(String id, Session session) {
    return get(id, Optional.of(session));
  }
  
  public Entity get(String id) {
    return get(id, Optional.empty());
  }

  private Entity get(String id, Optional<Session> session) {
    String url = getResourceUrl() + id;

    WSRequestHolder request = WS.url(url);
    
    if(authNeeded() && !session.isPresent()) {
      throw new NullPointerException("Auth needed, but session is not present");
    } else if(session.isPresent()) {
      request.setAuth(session.get().get("username"), session.get().get("password"));
    } 
    
    @SuppressWarnings("unchecked")
    Promise<Entity> response = request.get()
        .map(x -> x.getBody())
        .map(x -> (Entity) new Gson().fromJson(x, getType()));

    return response.get(1, TimeUnit.MINUTES);
  }
  
  public List<Entity> getList(Session session) {
    return getList(Optional.of(session));
  }
  
  public List<Entity> getList() {
    return getList(Optional.empty());
  }

  private List<Entity> getList(Optional<Session> session) {
    String url = getResourceUrl();
    
    WSRequestHolder request = WS.url(url);
    
    if(authNeeded() && !session.isPresent()) {
      throw new NullPointerException("Auth needed, but session is not present");
    } else if(session.isPresent()) {
      request.setAuth(session.get().get("username"), session.get().get("password"));
    } 

    @SuppressWarnings("unchecked")
    Promise<List<Entity>> response = request.get()
        .map(x -> x.getBody())
        .map(x -> (List<Entity>) new Gson().fromJson(x, getListType()));

    return response.get(1, TimeUnit.MINUTES);
  }
  
  public List<Entity> query(Map<String, String> params, Session session) {
    return query(params, Optional.of(session));
  }
  
  public List<Entity> query(Map<String, String> params) {
    return query(params, Optional.empty());
  }

  private List<Entity> query(Map<String, String> params, Optional<Session> session) {
    String url = getResourceUrl();
    
    WSRequestHolder request = WS.url(url + "query");
    
    if(authNeeded() && !session.isPresent()) {
      throw new NullPointerException("Auth needed, but session is not present");
    } else if(session.isPresent()) {
      request.setAuth(session.get().get("username"), session.get().get("password"));
    } 

    // set parameters
    for (Entry<String, String> param : params.entrySet()) {
      request.setQueryParameter(param.getKey(), param.getValue());
    }
    
    @SuppressWarnings("unchecked")
    Promise<List<Entity>> response = request.get()
        .map(x -> x.getBody())
        .map(x -> (List<Entity>) new Gson().fromJson(x, getListType()));

    return response.get(1, TimeUnit.MINUTES);
  }
  
  public String create(Entity entity, Session session) {
    return create(entity, Optional.of(session));
  }
  
  public String create(Entity entity) {
    return create(entity, Optional.empty());
  }
  
  private String create(Entity entity, Optional<Session> session) {
    String url = getResourceUrl();
    
    WSRequestHolder request = WS.url(url);
    
    if(authNeeded() && !session.isPresent()) {
      throw new NullPointerException("Auth needed, but session is not present");
    } else if(session.isPresent()) {
      request.setAuth(session.get().get("username"), session.get().get("password"));
    } 
    
    String json = new Gson().toJson(entity);
    
    WSResponse response = request
        .setContentType("application/json")
        .post(json)
        .get(1, TimeUnit.MINUTES);
    
    if(response.getStatus() == Status.BAD_REQUEST) {
      throw new IllegalArgumentException(response.getBody());
    } else {
      return Iterables.getOnlyElement(response.getAllHeaders().get("Location"));
    }
  }
  
  public Boolean delete(String id, Session session) {
    String url = getResourceUrl();
    
    Promise<Boolean> result = WS.url(url + id)
        .setAuth(session.get("username"), session.get("password"))
        .setContentType("application/json")
        .delete()
        .map(x->x.getStatus() == 200);

    return result.get(1, TimeUnit.MINUTES);
  }

  public abstract Type getType();

  public abstract Type getListType();

  public abstract String getResourceName();
  
  public abstract Boolean authNeeded();

}
