package controllers;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ubicollab.ubibazaar.core.User;

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

  protected static void storeUserInSession(User user) {
    // store credentials in session
    session("userid", user.getId());
    session("name", user.getName());
    session("username", user.getUsername());
    session("password", user.getPassword());
  }

  protected static User fetchUserFromSession() {
    // store credentials in session
    User u = new User();
    u.setId(session("userid"));
    u.setName(session("name"));
    u.setUsername(session("username"));
    u.setPassword(session("password"));
    return u;
  }

  protected static Optional<String> extractId(String url) {
    Pattern pattern = Pattern.compile("([0-91-f]{32})");
    Matcher matcher = pattern.matcher(url);
    if (matcher.find()) {
      return Optional.of(matcher.group(1));
    } else {
      return Optional.empty();
    }
  }

}
