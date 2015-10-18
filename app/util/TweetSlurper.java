package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.*;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.LineStringProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.jooq.lambda.Unchecked;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daniel Nesbitt
 */
public class TweetSlurper {

    // ------------- Variables -------------

    private final TweetBag tweets = new TweetBag();
    private final Client client;

    // ------------- Constructors -------------

    public TweetSlurper() {
        try {
            client = startFirehose(tweets::addTag);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ------------- Public -------------

    public final Map<String, Integer> gatherStats(int skip, int limit) {
        return tweets.tweetCounts(skip, limit);
    }

    public final void close() {
        client.stop();
    }

    // -------------------- Fire hose --------------------

    private static Client startFirehose(Consumer<String> tagConsumer) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Pattern hash = Pattern.compile("#\\w+");

        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(100000);

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        // Optional: set up some followings and track terms
        List<String> terms = Lists.newArrayList("twitter", "api");

        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint()
            .trackTerms(terms)
//        	.locations(Lists.newArrayList(new Location(new Location.Coordinate(-74d, 40d), new Location.Coordinate(-73d, 41d))))
            ;

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(
            System.getProperty("consumerKey"),
            System.getProperty("consumerSecret"),
            System.getProperty("token"),
            System.getProperty("tokenSecret")
        );

        ClientBuilder builder = new ClientBuilder()
            .name("Hosebird-Client-01")
            .hosts(hosebirdHosts)
            .authentication(hosebirdAuth)
            .endpoint(hosebirdEndpoint)
            .processor(new LineStringProcessor(msgQueue));

        Client client = builder.build();
        client.connect();

        CompletableFuture.runAsync(Unchecked.runnable(() -> {
            while (!client.isDone()) {
                String message = msgQueue.take();
                JsonNode tweet = mapper.readTree(message);
                if (tweet.has("text")) {
                    String text = tweet.get("text").textValue();
                    Matcher m = hash.matcher(text);
                    while (m.find()) {
                        tagConsumer.accept(m.group());
                    }
                }
            }
        }));
        return client;
    }

}
