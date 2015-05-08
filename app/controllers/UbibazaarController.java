package controllers;

import java.util.Optional;

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

}
