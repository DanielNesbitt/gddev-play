package controllers;

import com.fasterxml.jackson.databind.JsonNode;
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

    // ------------- Actions -------------

    public Result users() {
        List<User> users = UserService.list();
        return ok(Json.toJson(users));
    }

    public Result user(long id) {
        return play.mvc.Results.TODO;
    }

    public Result createUser() {
        JsonNode ur = request().body().asJson();
        User user = UserService.create(
            ur.get("username").asText(),
            ur.get("forename").asText(),
            ur.get("surname").asText()
        );
        return ok(Json.toJson(user));
    }

    public Result deleteUser(long id) {
        UserService.delete(id);
        return noContent();
    }

}
