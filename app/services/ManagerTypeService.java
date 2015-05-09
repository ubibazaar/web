package services;

import java.lang.reflect.Type;
import java.util.List;

import org.ubicollab.ubibazaar.core.ManagerType;

import com.google.common.reflect.TypeToken;

public class ManagerTypeService extends UbibazaarService<ManagerType> {

  @Override
  public String getResourceName() {
    return "manager_types";
  }

  @SuppressWarnings("serial")
  @Override
  public Type getType() {
    return new TypeToken<ManagerType>() {}.getType();
  }

  @SuppressWarnings("serial")
  @Override
  public Type getListType() {
    return new TypeToken<List<ManagerType>>() {}.getType();
  }
  
  @Override
  public Boolean authNeeded() {
    return false;
  }

}
