package controllers;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

import play.mvc.Controller;

public class UbibazaarController extends Controller {

  protected static Optional<String> getParam(String paramName) {
    String[] queryString = request().queryString().get(paramName);

    if (queryString != null && queryString.length > 0) {
      return Optional.of(queryString[0]);
    } else {
      return Optional.empty();
    }
  }

  protected static Map<String, String> getParams() {
    Map<String, String> params = Maps.newHashMap();
    request().queryString().entrySet()
        .forEach(queryParam -> params.put(queryParam.getKey(), queryParam.getValue()[0]));
    return params;
  }

}
