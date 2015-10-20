package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.WebSocket;
import util.StatBag;
import util.TweetSlurper;

import java.util.List;
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
            StatBag stats = new StatBag();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                while (true) {
                    Map<String, Integer> hashes = slurper.gatherStats(0, 5);
                    List<String> removals = stats.add(hashes);

                    ObjectNode result = Json.newObject();
                    result.set("columns", Json.toJson(stats.columns()));
                    result.set("unload", Json.toJson(removals));

                    out.write(result);
                    try {
                        Thread.sleep(750);
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

    private static JsonNode toJson(Map<String, Integer> stats, Map<String, Integer> oldHashes) {
        ObjectNode result = Json.newObject();

        ArrayNode columns = Json.newArray();
        stats.forEach((tag, count) -> {
            ArrayNode column = Json.newArray()
                .add(tag)
                .add(count);
            columns.add(column);
        });
        result.set("columns", columns);

        if (oldHashes.size() > 0) {
            ArrayNode unload = Json.newArray();
            oldHashes.forEach((tag, integer) -> unload.add(tag));
            result.set("unload", unload);
        }

        return result;
    }

}
