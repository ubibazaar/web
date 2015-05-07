package services;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ubicollab.ubibazaar.core.User;

import play.libs.F.Promise;
import play.libs.ws.WS;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class UserService extends UbibazaarService<User> {

  @Override
  public String getResourceName() {
    return "users";
  }
  
  @SuppressWarnings("serial")
  @Override
  public Type getType() {
    return new TypeToken<User>() {}.getType();
  }

  @SuppressWarnings("serial")
  @Override
  public Type getListType() {
    return new TypeToken<List<User>>() {}.getType();
  }
  
  public User getByUsername(String username) {
    String url = getResourceUrl() + "query";

    @SuppressWarnings("unchecked")
    Promise<User> response = WS.url(url)
        .setQueryParameter("username", username)
        .get()
        .map(x -> x.getBody())
        .map(x -> (User) new Gson().fromJson(x, getType()));

    return response.get(1, TimeUnit.MINUTES);
  }
  
}
