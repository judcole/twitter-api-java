package com.judcole.twitter.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The service class to manage collecting tweet data from the Twitter Sampled Stream API
 */
@Service
@Slf4j
public class TweetCollectorService {

    /**
     * Instantiates a new Tweet collector.
     *
     * @param tweetCollector the async tweet collector that does the work
     */
    public TweetCollectorService(TweetCollector tweetCollector) {

        log.info("Starting the Tweet Collector Service");

        // Start reading tweets from Twitter on a separate thread (asynchronously)
        log.info("About to start the Tweet Reader");
        try {
            tweetCollector.readTweetsFromTwitter();
        } catch (InterruptedException e) {
            log.info("Tweet Reader thread interrupted");
        }

        // Start processing the tweet blocks on a separate thread (asynchronously)
        log.info("About to start the Tweet Processor");
        try {
            tweetCollector.processTweetBlocks();
        } catch (InterruptedException e) {
            log.info("Tweet Block Processor thread interrupted");
        }
    }
}
