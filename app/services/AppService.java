package services;

import java.lang.reflect.Type;
import java.util.List;

import org.ubicollab.ubibazaar.core.App;

import com.google.common.reflect.TypeToken;

public class AppService extends UbibazaarService<App> {

  @Override
  public String getResourceName() {
    return "apps";
  }

  @SuppressWarnings("serial")
  @Override
  public Type getType() {
    return new TypeToken<App>() {}.getType();
  }

  @SuppressWarnings("serial")
  @Override
  public Type getListType() {
    return new TypeToken<List<App>>() {}.getType();
  }

}
