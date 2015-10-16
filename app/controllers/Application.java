package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.demo;
import views.html.presentation;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class Application extends Controller {

    public Result index() {
        return ok(presentation.render());
    }

    public Result demo() {
        return ok(demo.render());
    }

    public WebSocket<JsonNode> ws() {
        return WebSocket.whenReady((in, out) -> {
            // Send a single 'Hello!' message
            out.write(Json.newObject());

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                while (true) {
                    out.write(Json.newObject().put("message", "ping"));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new CancellationException();
                    }
                }
            });

            // For each event received on the socket,
            in.onMessage(System.out::println);

            // When the socket is closed.
            in.onClose(() -> future.cancel(true));
        });
    }

}
