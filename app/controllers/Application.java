package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.presentation;

public class Application extends Controller {

    public Result index() {
        return ok(presentation.render());
    }

    public Result foo() {
        return play.mvc.Results.TODO;
    }
}
