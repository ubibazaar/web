package services;

import java.lang.reflect.Type;
import java.util.List;

import org.ubicollab.ubibazaar.core.Manager;

import com.google.common.reflect.TypeToken;

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

}
