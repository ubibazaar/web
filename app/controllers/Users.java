package controllers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ubicollab.ubibazaar.core.App;
import org.ubicollab.ubibazaar.core.Installation;
import org.ubicollab.ubibazaar.core.User;

import akka.event.slf4j.Logger;
import akka.routing.GetRoutees;
import play.data.Form;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.mvc.Controller;
import play.mvc.Result;
import services.AppService;
import services.InstallationService;
import services.UserService;

public class Users extends Controller {

  public static Form<User> userForm = Form.form(User.class);

  public static Result registrationForm() {
    return ok(views.html.user_registration.render());
  }

  public static Result loginForm() {
    return ok(views.html.user_login.render());
  }

  public static Result register() {
    // load form user data
    User user = userForm.bindFromRequest().get();
  
    // create user
    String createdUserResourceUrl = new UserService().create(user);
  
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
      List<Installation> installations = new InstallationService().getForUser(entered);

      // look up this user to get id and name
      User lookedUp = new UserService().getByUsername(entered.getUsername());

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
      return forbidden(views.html.user_login.render());
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
    User user = new UserService().get(id);

    // find apps by user
    // FIXME filter apps by user
    List<App> apps = new AppService().getList();

    return ok(views.html.user_profile.render(user, apps));
  }

}
