package services;

import java.lang.reflect.Type;
import java.util.List;

import org.ubicollab.ubibazaar.core.Installation;

import com.google.common.reflect.TypeToken;

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
  
  @Override
  public Boolean authNeeded() {
    return true;
  }
  
}
