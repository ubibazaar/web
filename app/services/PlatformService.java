package services;

import java.lang.reflect.Type;
import java.util.List;

import org.ubicollab.ubibazaar.core.Platform;

import com.google.common.reflect.TypeToken;

public class PlatformService extends UbibazaarService<Platform> {

  @Override
  public String getResourceName() {
    return "platforms";
  }

  @SuppressWarnings("serial")
  @Override
  public Type getType() {
    return new TypeToken<Platform>() {}.getType();
  }

  @SuppressWarnings("serial")
  @Override
  public Type getListType() {
    return new TypeToken<List<Platform>>() {}.getType();
  }

}
