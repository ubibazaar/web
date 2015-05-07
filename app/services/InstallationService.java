package services;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ubicollab.ubibazaar.core.Installation;
import org.ubicollab.ubibazaar.core.User;

import play.libs.F.Promise;
import play.libs.ws.WS;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class InstallationService extends UbibazaarService<Installation> {

  @Override
  public String getResourceName() {
    return "installations";
  }

  @SuppressWarnings("serial")
  @Override
  public Type getType() {
    return new TypeToken<Installation>() {}.getType();
  }

  @SuppressWarnings("serial")
  @Override
  public Type getListType() {
    return new TypeToken<List<Installation>>() {}.getType();
  }
  
  //FIXME this is nasty.. do something about it
  public List<Installation> getForUser(User user) {
    String url = getResourceUrl();

    @SuppressWarnings("unchecked")
    Promise<List<Installation>> response = WS.url(url)
        .setAuth(user.getUsername(), user.getPassword())
        .get()
        .map(x -> x.getBody())
        .map(x -> (List<Installation>) new Gson().fromJson(x, getListType()));

    return response.get(1, TimeUnit.MINUTES);
  }

}
