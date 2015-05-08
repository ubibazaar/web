package services;

import java.lang.reflect.Type;
import java.util.List;

import org.ubicollab.ubibazaar.core.Device;

import com.google.common.reflect.TypeToken;

public class DeviceService extends UbibazaarService<Device> {

  @Override
  public String getResourceName() {
    return "devices";
  }

  @SuppressWarnings("serial")
  @Override
  public Type getType() {
    return new TypeToken<Device>() {}.getType();
  }

  @SuppressWarnings("serial")
  @Override
  public Type getListType() {
    return new TypeToken<List<Device>>() {}.getType();
  }
  
  @Override
  public Boolean authNeeded() {
    return true;
  }

}
