package controllers;

import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.UserService;

import java.util.List;

/**
 * @author Daniel Nesbitt
 */
public class Users extends Controller {

    public Result users() {
        List<User> users = UserService.list();
        return ok(Json.toJson(users));
    }

    public Result user(String id) {
        return play.mvc.Results.TODO;
    }

}
