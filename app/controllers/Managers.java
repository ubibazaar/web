package controllers;

import java.util.List;
import java.util.Optional;

import org.ubicollab.ubibazaar.core.Device;
import org.ubicollab.ubibazaar.core.Manager;
import org.ubicollab.ubibazaar.core.ManagerType;

import play.mvc.Result;
import services.UbibazaarService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public class Managers extends UbibazaarController {

  public static Result forPlatform(String platformId, String deviceId) {
    ImmutableMap<String, String> query = ImmutableMap.of("platform", platformId);
    List<ManagerType> managerTypes = UbibazaarService.managerTypeService.query(query);

    if (managerTypes.isEmpty()) {
      // FIXME none for this type
      return status(500);
    } else if (managerTypes.size() > 1) {
      // FIXME give option to choose
      return status(404);
    } else {
      // redirect to the only one
      ManagerType onlyManagerType = Iterables.getOnlyElement(managerTypes);
      return forType(onlyManagerType.getId(), deviceId);
    }
  }

  public static Result forType(String managerTypeId, String deviceId) {
    ManagerType managerType = UbibazaarService.managerTypeService.get(managerTypeId);
    Device device = UbibazaarService.deviceService.get(deviceId, session());
    
    // create manager
    Manager manager = new Manager();
    manager.setOwner(fetchUserFromSession());
    manager.setType(managerType);
    String url = UbibazaarService.managerService.create(manager, session());

    Optional<String> managerId = extractId(url);

    if (managerId.isPresent()) {
      manager.setId(managerId.get());
      
      if(UbibazaarService.managerService.linkDeviceToManager(manager, device, session())) {
        // FIXME show instructions..
        return redirect("/devices/" + deviceId);
      }
    } 
    
    // FIXME show error message
    return status(500);
  }

}
