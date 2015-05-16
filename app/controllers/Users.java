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
    return ok(user_registration.render(Optional.<String>empty()));
  }

  public static Result loginForm() {
    // this trick makes the message in the login form disappear once 
    // first clean the step2 cookie
    session().remove("afterlogin_redir2");
    // and only if step1 was set now
    if(session("afterlogin_redir") != null) {
      // set step2
      session("afterlogin_redir2",session("afterlogin_redir"));
      //and then clean the step1
      session().remove("afterlogin_redir");
    }
    
    return ok(user_login.render(Optional.<String>empty()));
  }

  public static Result register() {
    try {
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
        return redirect("/profile");
      } else {
        throw new RuntimeException("No user id in response.");
      }
    } catch(IllegalArgumentException e) {
      return ok(user_registration.render(Optional.of(e.getMessage())));
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

        // redirect to originally accessed URL, if there was any
        if(session("afterlogin_redir2") != null) {
          String session = session("afterlogin_redir2");
          session().remove("afterlogin_redir2");
          return redirect(session);
        }
        
        // redirect to profile page
        return redirect("/profile");
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

  public static Result profile() {
    String id = fetchUserFromSession().getId();
    
    // find user
    User user = UbibazaarService.userService.get(id);

    // find apps by user
    List<App> apps = UbibazaarService.appService.query(ImmutableMap.of("author", id));

    return ok(user_profile.render(user, apps));
  }

}
