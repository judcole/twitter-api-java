package com.judcole.twitter.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.judcole.twitter.shared.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;
import static java.time.LocalDateTime.now;

/**
 * The class to collect tweet data from the Twitter Sampled Stream API
 */
@Component
@Slf4j
public class TweetCollector {

    // Regular expression pattern to match hashtags
    private static final String hashtagPattern = "\\B#\\w*[a-zA-Z]+\\w*";

    /**
     * The constant HashtagRegex.
     */
// Regular expression object to match hashtags
    public static final Pattern hashtagRegex = Pattern.compile(hashtagPattern, Pattern.CASE_INSENSITIVE);

    // Name of bearer token environment variable
    private final String bearerTokenEnvironmentString = "STREAM_BEARER_TOKEN";

    // Message to display and send to the API if the token is missing
    private final String bearerTokenMissingMessage = "To access the Twitter API please set the " + bearerTokenEnvironmentString + " environment variable";

    // URL of Twitter stream API
    private final String twitterApiUrl = "https://api.twitter.com/2/tweets/sample/stream";

    // Twitter stream API authentication bearer token (read from the environment)
    private final String bearerToken = System.getenv(bearerTokenEnvironmentString);

    // Dictionary of all Hashtags and their counts
    private final HashMap<String, Long> hashtagDictionary = new HashMap<>();

    // HTTP client for accessing the Twitter API
    private final HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();

    // Object mapper for deserializing tweet JSON
    private final ObjectMapper mapper = new ObjectMapper();

    // Shared total statistics
    private final SampledStreamStats stats;

    // Shared background queue
    private final IBackgroundQueue<TweetBlock> tweetQueue;

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
    }

    /**
     * Read tweets from twitter completable future.
     *
     * @return the completable future
     * @throws InterruptedException the interrupted exception
     */
    @Async
    public CompletableFuture<Boolean> readTweetsFromTwitter() throws InterruptedException {

        log.info("Starting the Tweet Reader");

        if (bearerToken == null) {
            // No bearer token so log it and indicate it in the statistics data
            log.error(bearerTokenMissingMessage);
            stats.setStatus(bearerTokenMissingMessage);
            return CompletableFuture.completedFuture(false);
        }

        try {
            final URIBuilder uriBuilder = new URIBuilder(twitterApiUrl);

            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", String.format("Bearer %s", bearerToken));

            // Initiate the connection with the Twitter stream
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (null != entity) {
                // Set up a reader and get the first line
                BufferedReader reader = new BufferedReader(new InputStreamReader((entity.getContent())));
                String line = reader.readLine();

                // Loop until the end of the stream or cancelled
                while (line != null) {
//                    log.info(line);

                    if (!StringUtils.isBlank(line)) {
                        // Not just a keep alive so create a new block instance and enqueue it
                        tweetQueue.enqueue(new TweetBlock(line));
                    }

                    // Read the next line
                    line = reader.readLine();
                }
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        return CompletableFuture.completedFuture(true);
    }

    /**
     * Process an incoming tweet block from the queue.
     *
     * @param tweetBlock the tweet block
     */
    private void ProcessTweetBlockFromQueue(TweetBlock tweetBlock) {
        // Prepare counts for the block
        long tweetCount = 0;
        long hashtagCount = 0;

        try {
            // Deserialize the tweet block
            Tweet tweet = mapper.readValue(tweetBlock.Contents, Tweet.class);

            if ((tweet != null) && (tweet.data != null) && (tweet.data.text != null)) {
                // It looks valid so use it
                tweetCount++;

                // Look for hashtags
                var matcher = hashtagRegex.matcher(tweet.data.text);

                // Loop through the results
                while (matcher.find()) {
                    // Get the hashtag without the leading hash
                    var hashtag = matcher.group().substring(1);
//                    log.info(hashtag);

                    // One more hashtag found
                    hashtagCount++;

                    // Increment the counter for this tag
                    var hashtagLower = hashtag.toLowerCase();
                    var newCount = hashtagDictionary.getOrDefault(hashtagLower, 0L) + 1;
                    hashtagDictionary.put(hashtagLower, newCount);

                    // Update the list of top hashtags with a specified hashtag and count
                    stats.UpdateTopHashtags(hashtag, newCount);
                }
            }

            long totalTweets = stats.getTotalTweets() + tweetCount;

            // Log a message every 100 tweets
            if (totalTweets % 100 == 0) {
                log.info("Have now processed {} tweets", totalTweets);
            }

            // Save the new statistics from the block
            stats.SetBasicFields(stats.getTotalHashtags() + hashtagCount, totalTweets, tweetQueue.getCount());
        } catch (Exception ex) {
            log.error("An error occurred when processing tweets: Exception: ", ex);
            stats.setStatus("An error occurred when processing tweets: Exception: " + ex);
        }
    }

    /**
     * Gets a result (temporary test).
     *
     * @return the result
     * @throws InterruptedException the interrupted exception
     */
    @Async
    public CompletableFuture<Boolean> processTweetBlocks() throws InterruptedException {

        log.info("Starting the Tweet Processor");

        int messageSeconds = 0;
        while (messageSeconds < 100) {
            try {
                // Get the current time for various calculations
                var time = now(ZoneOffset.UTC);
                var seconds = time.getSecond();

                if ((seconds % 10 == 0) && (seconds != messageSeconds)) {
                    // Log a message every 10 seconds or so
                    log.info("Worker running at: {} with {} tweets queued", time, tweetQueue.getCount());

                    // Remember we have done so for this second so we don't log twice
                    messageSeconds = seconds;
                }

                if (tweetQueue.getCount() > 0) {
                    // Get the next tweet block to process
                    var tweetBlock = tweetQueue.dequeue();

                    if (tweetBlock != null) {
//                        log.info("Processing tweet block at: {}", time);

                        // Process the tweet block
                        ProcessTweetBlockFromQueue(tweetBlock);
                    }
                } else {
                    // There are no more tweets to process so wait a little while
                    sleep(100);
                }
            } catch (Exception ex) {
                log.error("An error occurred when reading tweets: Exception: ", ex);
                stats.setStatus("An error occurred when reading tweets: Exception: " + ex);
            }
        }

        return CompletableFuture.completedFuture(true);
    }
}
