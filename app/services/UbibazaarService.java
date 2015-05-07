package services;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import play.libs.F.Promise;
import play.libs.ws.WS;

import com.google.gson.Gson;

public abstract class UbibazaarService<Entity> {

  public static final String API_URL = "http://ubibazaar.io:8080/ubibazaar-api";

  private String getResourceUrl() {
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

  public abstract Type getType();

  public abstract Type getListType();

  public abstract String getResourceName();

}
