package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.WebSocket;
import util.TweetSlurper;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

/**
 * @author Daniel Nesbitt
 */
public class HashTags extends Controller {

    // ------------- Actions -------------

    public WebSocket<JsonNode> ws() {
        return WebSocket.whenReady((in, out) -> {
            TweetSlurper slurper = new TweetSlurper();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                while (true) {
                    out.write(toJson(slurper.gatherStats(0, 5)));
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        throw new CancellationException();
                    }
                }
            });
            in.onClose(() -> {
                future.cancel(true);
                slurper.close();
            });
        });
    }

    // ------------- Private -------------

    private static JsonNode toJson(Map<String, Integer> stats) {
        ArrayNode labels = Json.newArray();
        ArrayNode data = Json.newArray();

        stats.forEach((k,v) -> {
            labels.add(k);
            data.add(v);
        });

        ObjectNode result = Json.newObject();
        result.set("labels", labels);
        result.set("datasets",
            Json.newObject()
                .put("label", "Top Tweets")
                .put("fillColor", "rgba(220,220,220,0.5)")
                .put("strokeColor", "rgba(220,220,220,0.8)")
                .put("highlightFill", "rgba(220,220,220,0.75)")
                .put("highlightStroke", "rgba(220,220,220,1)")
                .set("data", data)
        );

        return result;
    }

}
