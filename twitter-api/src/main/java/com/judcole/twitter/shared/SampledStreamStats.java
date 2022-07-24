package com.judcole.twitter.shared;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The class to store statistics for the sampled stream.
 */
@Component
public class SampledStreamStats {

    @Getter
    // Average daily number of tweets received
    private long dailyTweets;

    // Average hourly number of tweets received
    @Getter
    private long hourlyTweets;

    // Date and time of statistics
    @Getter
    private LocalDateTime lastUpdated;

    // Extra status information
    @Getter
    @Setter
    private String status;

    // List of top Hashtag counts
    @Getter
    private final long[] topHashtagCounts;

    // List of top 10 hashtags
    @Getter
    private final String[] topHashtags;

    // Size of the list for the top Hashtags
    @Getter
    private final int topHashtagsSize;

    // Total number of hashtags received
    @Getter
    private long totalHashtags;

    // Total number of tweets received
    @Getter
    private long totalTweets;

    // Number of Tweets waiting to be processed in incoming queue
    @Getter
    private int tweetQueueCount;

    // Object to use for simple locking when updating complex fields
    private final Lock statsLock = new ReentrantLock();

    /**
     * Construct the SampledStreamStats instance with the default table size.
     */
    public SampledStreamStats() { this(10); }

    /**
     * Construct the SampledStreamStats instance with a specified table size.
     *
     * @param topHashtagsSize the size of the list for the top Hashtags
     */
    public SampledStreamStats(int topHashtagsSize) {
        // Set the last updated date and time
        lastUpdated = LocalDateTime.now(ZoneOffset.UTC);

        // Create the top hashtags list
        this.topHashtagsSize = topHashtagsSize;
        topHashtagCounts = new long[this.topHashtagsSize];
        topHashtags = new String[this.topHashtagsSize];
    }

    /**
     * Set new values for the basic fields (concurrent safe).
     *
     * @param totalHashtags   the total hashtags
     * @param totalTweets     the total tweets
     * @param tweetQueueCount the tweet queue count
     */
    public void SetBasicFields(long totalHashtags, long totalTweets, int tweetQueueCount) {
        // Play safe and lock the instance while we update it
        statsLock.lock();
        try {
            this.totalHashtags = totalHashtags;
            this.totalTweets = totalTweets;
            this.tweetQueueCount = tweetQueueCount;
        } finally {
            statsLock.unlock();
        }
    }

    /**
     * Calculate and set all calculated fields (concurrent safe).
     *
     * @param startTime the application start time for evaluating the elapsed time
     */
    public void SetCalculatedFields(LocalDateTime startTime) {
        // Play safe and lock the instance while we update it
        statsLock.lock();
        try {
            // Update the last updated date and time
            lastUpdated = LocalDateTime.now(ZoneOffset.UTC);

            // Calculate and set the daily tweet rate with a check for negative durations
            var elapsedTime = ChronoUnit.SECONDS.between(startTime, lastUpdated);
            var elapsedDays = Math.max(1, Math.ceil((double) elapsedTime / (60 * 60 * 24)));
            dailyTweets = (long) (Math.ceil((double) totalTweets) / elapsedDays);

            // Calculate and set the hourly tweet rate with a check for negative durations
            var elapsedHours = Math.max(1, Math.ceil((double) elapsedTime / (60 * 60)));
            hourlyTweets = (long) (Math.ceil((double) totalTweets) / elapsedHours);
        } finally {
            statsLock.unlock();
        }
    }

    /**
     * Update the list of top hashtags with a specified hashtag and count.
     *
     * @param hashtag the hashtag to add
     * @param count   the count of occurrences of the hashtag
     */
    public void UpdateTopHashtags(String hashtag, long count) {
        // Index of the current hashtag
        int index;

        // Play safe and lock the instance while we update it
        statsLock.lock();
        try {
            // Find if it is already in the list and if so delete it
            for (index = 0; index < topHashtagsSize; index++)
                if (hashtag.equalsIgnoreCase(topHashtags[index])) {
                    // Found the hashtag so shuffle the rest up
                    for (int i = index; i < topHashtagsSize - 1; i++) {
                        // Shuffle the lower hashtags up a slot
                        topHashtags[i] = topHashtags[i + 1];
                        topHashtagCounts[i] = topHashtagCounts[i + 1];
                    }
                    // Clear the last slot to make sure it can be overwritten
                    topHashtags[topHashtagsSize - 1] = null;
                    topHashtagCounts[topHashtagsSize - 1] = 0;

                    // Done with this part
                    break;
                }

            // Find if and where the hashtag qualifies to be in the list
            for (index = 0; (index < topHashtagsSize) && (topHashtagCounts[index] > count); ) {
                index++;
            }

            if (index < topHashtagsSize) {
                // Found its slot so shuffle the rest down to make room
                for (int i = topHashtagsSize - 2; i >= index; i--) {
                    // Shuffle the hashtag down a slot
                    topHashtags[i + 1] = topHashtags[i];
                    topHashtagCounts[i + 1] = topHashtagCounts[i];
                }

                // Set the new hashtag value and count in its correct slot
                topHashtags[index] = hashtag;
                topHashtagCounts[index] = count;
            }
        } finally {
            statsLock.unlock();
        }
    }
}
