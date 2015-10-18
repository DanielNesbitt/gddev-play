package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.WebSocket;
import util.TweetSlurper;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;

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
        return stats.entrySet().stream()
            .collect(Collector.of(
                Json::newObject,
                (node, e) -> node.put(e.getKey(), e.getValue()),
                (n1, n2) -> (ObjectNode) n1.setAll(n2)
            ));
    }

}
