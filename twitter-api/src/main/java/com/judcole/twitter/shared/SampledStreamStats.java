package com.judcole.twitter.shared;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The class to store statistics for the sampled stream.
 */
public class SampledStreamStats {

    @Getter(AccessLevel.PUBLIC)
    // Average daily number of tweets received
    private long dailyTweets;

    // Average hourly number of tweets received
    @Getter(AccessLevel.PUBLIC)
    private long HourlyTweets;

    // Date and time of statistics
    @Getter(AccessLevel.PUBLIC)
    private LocalDateTime LastUpdated;

    // Extra status information
    @Getter
    @Setter
    private String Status;

    // List of top Hashtag counts
    @Getter(AccessLevel.PUBLIC)
    private final long[] TopHashtagCounts;

    // List of top 10 hashtags
    @Getter(AccessLevel.PUBLIC)
    private final String[] TopHashtags;

    // Size of the list for the top Hashtags
    @Getter(AccessLevel.PUBLIC)
    public int TopHashtagsSize;

    // Total number of hashtags received
    @Getter(AccessLevel.PUBLIC)
    private long TotalHashtags;

    // Total number of tweets received
    @Getter(AccessLevel.PUBLIC)
    private long TotalTweets;

    // Number of Tweets waiting to be processed in incoming queue
    @Getter(AccessLevel.PUBLIC)
    private int TweetQueueCount;

    // Object to use for simple locking when updating complex fields
    private final Lock statsLock = new ReentrantLock();

    /**
     * Construct the SampledStreamStats instance with the current date and time.
     *
     * @param topHashtagsSize the size of the list for the top Hashtags
     */
    public SampledStreamStats(int topHashtagsSize) {
        // Set the last updated date and time
        LastUpdated = LocalDateTime.now(ZoneOffset.UTC);

        // Create the top hashtags list
        TopHashtagsSize = topHashtagsSize;
        TopHashtagCounts = new long[TopHashtagsSize];
        TopHashtags = new String[TopHashtagsSize];
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
            TotalHashtags = totalHashtags;
            TotalTweets = totalTweets;
            TweetQueueCount = tweetQueueCount;
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
            LastUpdated = LocalDateTime.now(ZoneOffset.UTC);

            // Calculate and set the daily tweet rate with a check for negative durations
            var elapsedTime = ChronoUnit.SECONDS.between(startTime, LastUpdated);
            var elapsedDays = Math.max(1, Math.ceil((double) elapsedTime / (60 * 60 * 24)));
            dailyTweets = (long) (Math.ceil((double) TotalTweets) / elapsedDays);

            // Calculate and set the hourly tweet rate with a check for negative durations
            var elapsedHours = Math.max(1, Math.ceil((double) elapsedTime / (60 * 60)));
            HourlyTweets = (long) (Math.ceil((double) TotalTweets) / elapsedHours);
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
            for (index = 0; index < TopHashtagsSize; index++)
                if (hashtag.equalsIgnoreCase(TopHashtags[index])) {
                    // Found the hashtag so shuffle the rest up
                    for (int i = index; i < TopHashtagsSize - 1; i++) {
                        // Shuffle the lower hashtags up a slot
                        TopHashtags[i] = TopHashtags[i + 1];
                        TopHashtagCounts[i] = TopHashtagCounts[i + 1];
                    }
                    // Clear the last slot to make sure it can be overwritten
                    TopHashtags[TopHashtagsSize - 1] = null;
                    TopHashtagCounts[TopHashtagsSize - 1] = 0;

                    // Done with this part
                    break;
                }

            // Find if and where the hashtag qualifies to be in the list
            for (index = 0; (index < TopHashtagsSize) && (TopHashtagCounts[index] > count); ) {
                index++;
            }

            if (index < TopHashtagsSize) {
                // Found its slot so shuffle the rest down to make room
                for (int i = TopHashtagsSize - 2; i >= index; i--) {
                    // Shuffle the hashtag down a slot
                    TopHashtags[i + 1] = TopHashtags[i];
                    TopHashtagCounts[i + 1] = TopHashtagCounts[i];
                }

                // Set the new hashtag value and count in its correct slot
                TopHashtags[index] = hashtag;
                TopHashtagCounts[index] = count;
            }
        } finally {
            statsLock.unlock();
        }
    }
}
