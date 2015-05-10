package controllers;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.User;

import com.google.common.collect.ImmutableMap;

import play.data.Form;
import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

public class Users extends UbibazaarController {

  private static final String SERVER_ERROR = "Server error. Please try again later.";
  private static final String INCORRECT_CREDENTIALS = "Sorry, the credentials are not correct. Please check your username and password and try again.";
  
  public static Form<User> userForm = Form.form(User.class);

  public static Result registrationForm() {
    return ok(user_registration.render());
  }

  public static Result loginForm() {
    return ok(user_login.render(Optional.<String>empty()));
  }

  public static Result register() {
    // load form user data
    User user = userForm.bindFromRequest().get();

    // create user
    String createdUserResourceUrl = UbibazaarService.userService.create(user);

    // find created user id
    Pattern pattern = Pattern.compile("([0-91-f]{32})");
    Matcher matcher = pattern.matcher(createdUserResourceUrl);
    if (matcher.find()) {
      String id = matcher.group(1);
      user.setId(id);

      storeUserInSession(user);

      // redirect to profile page
      return redirect("/users/" + id);
    } else {
      throw new RuntimeException("No user id in response.");
    }
  }

  public static Result login() {
    // load form user data
    User entered = userForm.bindFromRequest().get();

    try {
      // find installations
      // this requires authentication, so we know if the credentials are ok or not
      boolean success = UbibazaarService.userService.acessSecured(entered);

      if (success) {
        // look up this user to get id and name
        User user = UbibazaarService.userService.getByUsername(entered.getUsername());
        user.setPassword(entered.getPassword());

        // store credentials in session
        storeUserInSession(user);

        // redirect to profile page
        return redirect("/users/" + user.getId());
      } else {
        // fail, show alert
        return forbidden(user_login.render(Optional.of(INCORRECT_CREDENTIALS)));
      }
    } catch (Exception e) {
      play.Logger.error(e.getMessage(), e);
      return forbidden(user_login.render(Optional.of(SERVER_ERROR)));
    }
  }

  public static Result logout() {
    // clear all session data
    session().clear();

    // redirect to login form
    return redirect("/login");
  }

  public static Result profile(String id) {
    // find user
    User user = UbibazaarService.userService.get(id);

    // find apps by user
    List<App> apps = UbibazaarService.appService.query(ImmutableMap.of("author", id));

    return ok(user_profile.render(user, apps));
  }

}
