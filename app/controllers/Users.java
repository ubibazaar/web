package controllers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Installation;
import org.ubicollab.ubibazaar.core.User;

import com.google.common.collect.ImmutableMap;

import play.data.Form;
import play.mvc.Result;
import services.UbibazaarService;
import views.html.*;

public class Users extends UbibazaarController {

  public static Form<User> userForm = Form.form(User.class);

  public static Result registrationForm() {
    return ok(user_registration.render());
  }

  public static Result loginForm() {
    return ok(user_login.render());
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
  
      // store credentials in session
      session("userid", id);
      session("name", user.getName());
      session("username", user.getUsername());
      session("password", user.getPassword());
  
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
      List<Installation> installations = UbibazaarService.installationService.getForUser(entered);

      // look up this user to get id and name
      User lookedUp = UbibazaarService.userService.getByUsername(entered.getUsername());

      // store credentials in session
      session("userid", lookedUp.getId());
      session("name", lookedUp.getName());
      session("username", lookedUp.getUsername());
      session("password", entered.getPassword());

      // redirect to profile page
      return redirect("/users/" + lookedUp.getId());
    } catch (Exception e) {
      play.Logger.error(e.getMessage(), e);
      // FIXME write more on the failed login
      return forbidden(user_login.render());
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
    List<App> apps = UbibazaarService.appService.query(ImmutableMap.of("user", id));

    return ok(user_profile.render(user, apps));
  }

}
