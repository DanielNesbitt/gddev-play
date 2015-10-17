package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.demo;
import views.html.presentation;

public class Application extends Controller {

    public Result index() {
        return ok(presentation.render());
    }

    public Result demo() {
        return ok(demo.render());
    }

}
