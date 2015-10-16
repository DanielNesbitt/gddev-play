package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import services.UserService;

/**
 * @author Daniel Nesbitt
 */
public class Users extends Controller {

    // ------------- Actions -------------

    public Result users() {
        return Results.TODO;
    }

    public Result user(long id) {
        return Results.TODO;
    }

    public Result createUser() {
        JsonNode json = request().body().asJson();
        User user = UserService.create(
            json.get("username").asText(),
            json.get("forename").asText(),
            json.get("surname").asText()
        );
        return ok(Json.toJson(user));
    }

    public Result deleteUser(long id) {
        UserService.delete(id);
        return noContent();
    }

}
