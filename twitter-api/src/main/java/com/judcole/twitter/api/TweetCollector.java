package com.judcole.twitter.api;

import com.judcole.twitter.shared.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.yaml.snakeyaml.reader.StreamReader;

import java.net.http.HttpClient;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * The class to collect tweet data from the Twitter Sampled Stream API
 */
@Service
@Slf4j
public class TweetCollector {

    // Regular expression pattern to match hashtags
    private static final String hashtagPattern = "\\B#\\w*[a-zA-Z]+\\w*";

    // Regular expression object to match hashtags
    public static final Pattern HashtagRegex = Pattern.compile(hashtagPattern, Pattern.CASE_INSENSITIVE);

    // Name of bearer token environment variable
    private final String BearerTokenEnvironmentString = "STREAM_BEARER_TOKEN";

    // Message to display and send to the API if the token is missing
    private final String BearerTokenMissingMessage =
            "To access the Twitter API please set the " + BearerTokenEnvironmentString + " environment variable";

    // URL of Twitter stream API
    private final String TwitterApiUrl = "https://api.twitter.com/2/tweets/sample/stream";

    // Twitter stream API authentication bearer token (read from the environment)
    private final String _bearerToken = System.getenv(BearerTokenEnvironmentString);

    // Dictionary of all Hashtags and their counts
    private final HashMap<String, Long> _hashtagDictionary= new HashMap<>();

    // HTTP client for accessing the Twitter API
    private final WebClient _httpClient = WebClient.builder()
            .baseUrl(TwitterApiUrl)
            .build();

    // Shared total statistics
    private final SampledStreamStats stats;

    // Shared background queue
    private final IBackgroundQueue<TweetBlock> tweetQueue;

    // Stream reader for accessing the Twitter API
    private StreamReader _tweetStreamReader;

    /**
     * Instantiates a new Tweet collector.
     *
     * @param queueFactory the queue factory
     * @param statsFactory the stats factory
     */
    public TweetCollector(BackgroundQueueFactory queueFactory, SampledStreamStatsFactory statsFactory) {
        // Save the shared stats parameter
        stats = statsFactory.getStatsInstance(SampledStreamStatsFactory.DEFAULT_STATS_SIZE);
        // Save the shared queue instance
        tweetQueue = queueFactory.getBackgroundQueueInstance(BackgroundQueueFactory.DEFAULT_QUEUE_SIZE);

        log.info("Starting the Tweet Collector");

        try {
            getResult("1111");
        } catch (InterruptedException e) {
            log.info("Tweet Collector thread interrupted");
        }
    }

    /**
     * Gets result.
     *
     * @param id the id
     * @return the result
     * @throws InterruptedException the interrupted exception
     */
    @Async
    public CompletableFuture<Boolean> getResult(final String id) throws InterruptedException {
        for (int i = 1; i < 1000; i++) {
            log.info("Filling the customer details for id {} ", id);
            // Doing an artificial sleep
            Thread.sleep(10000);
        }
        return CompletableFuture.completedFuture(true);
    }
}
