package util.actor;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.WebSocket;
import util.TweetSlurper;

import java.util.Map;

/**
 * @author Daniel Nesbitt
 */
public class StatsGatheringActor extends AbstractActor {

    private final WebSocket.Out<JsonNode> out;
    private final TweetSlurper tweetSlurper = new TweetSlurper();

    public StatsGatheringActor(WebSocket.Out<JsonNode> out) {
        this.out = out;
        receive(ReceiveBuilder
            .match(Gather.class, this::writeStats)
            .build()
        );
    }

    private void writeStats(Gather g) {
        Map<String, Integer> topHashTags = tweetSlurper.gatherStats(0, 5);

        ArrayNode labels = Json.newArray();
        ArrayNode data = Json.newArray();

        topHashTags.forEach((k,v) -> {
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

        out.write(result);
    }

    // ------------- Inner classes -------------

    public static class Gather { }

}
