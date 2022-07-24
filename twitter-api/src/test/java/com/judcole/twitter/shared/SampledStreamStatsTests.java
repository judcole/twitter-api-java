package com.judcole.twitter.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Class for testing SampleStreamStats.
 */
class SampledStreamStatsTests {

    // Some test values
    private final String hashtag1 = "abc";
    private final String hashtag2 = "abc1";
    private final String hashtag3 = "abc12";
    private final String hashtag4 = "xyz";
    private final String hashtag5 = "alonghashtag";

    /**
     * Test that setting new values for the basic fields is successful.
     *
     * @param totalHashtags   the total hashtags
     * @param totalTweets     the total tweets
     * @param tweetQueueCount the tweet queue count
     */
    @ParameterizedTest
    @CsvSource({"0, 0, 0", "1, 0, 0", "0, 1, 0", "0, 0, 1", "10, 20, 0", "10, 0, 20", "20, 30, 40"})
    void setBasicFields_SetValues_ReturnsCorrectValues(long totalHashtags, long totalTweets, int tweetQueueCount) {
        var stats = CreateStatsInstance(1);

        // Set the basic fields
        stats.SetBasicFields(totalHashtags, totalTweets, tweetQueueCount);

        // Check the result
        assertThat(stats.getTotalHashtags()).isEqualTo(totalHashtags);
        assertThat(stats.getTotalTweets()).isEqualTo(totalTweets);
        assertThat(stats.getTweetQueueCount()).isEqualTo(tweetQueueCount);
    }

    /**
     * Test that calculating the calculated fields from the total tweet count is successful.
     *
     * @param totalTweets    the total tweets
     * @param elapsedHours   the elapsed hours
     * @param expectedDaily  the expected daily
     * @param expectedHourly the expected hourly
     */
    @ParameterizedTest
    @CsvSource({"0, 0, 0, 0", "1, 0, 1, 1", "99, 0, 99, 99", "0, 1, 0, 0", "1, 1, 1, 1", "99, 1, 99, 99", "0, 12345, 0, 0", "1, 12345, 0, 0", "98, 12345, 0, 0", "9999, 12345, 19, 0", "99999, 12345, 194, 8", "2000000, 12345, 3883, 162", "0, 54321, 0, 0", "1, 54321, 0, 0", "97, 54321, 0, 0", "9999, 54321, 4, 0", "99999, 54321, 44, 1", "2000000, 54321, 883, 36"})
    void setCalculatedFields_SetValues_ReturnsCorrect(long totalTweets, int elapsedHours, long expectedDaily, long expectedHourly) {
        var stats = CreateStatsInstance(1);
        stats.SetBasicFields(0, totalTweets, 0);

        // Set a start date from the data allowing a margin of a second for the test
        var startDate = stats.getLastUpdated().plusHours(-elapsedHours).plusSeconds(1);

        // Calculate and set the calculated fields
        stats.SetCalculatedFields(startDate);

        // Check the result
        assertThat(stats.getDailyTweets()).isEqualTo(expectedDaily);
        assertThat(stats.getHourlyTweets()).isEqualTo(expectedHourly);
    }

/// <summary>
    /// Test that the creation of a new SampledStreamStats object is successful
    /// </summary>
    /// <param name="topHashtagsSize">Size of the list for the top Hashtags</param>
    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 100})
    public void SampledStreamStats_Create_ReturnInstance(int topHashtagsSize)
    {
        // Save the current time as the expected default time
        var expectedDate = LocalDateTime.now(ZoneOffset.UTC);

        // Create an instance with the default date and time
        var stats = CreateStatsInstance(topHashtagsSize);

        // Check the default values
        assertThat(stats).isNotNull();
        assertThat(stats.getDailyTweets()).isZero();
        assertThat(stats.getHourlyTweets()).isZero();
        assertThat(stats.getLastUpdated().toLocalDate()).isEqualTo(expectedDate.toLocalDate());
        assertThat(stats.getStatus()).isNull();

        assertThat(stats.TopHashtagsSize).isEqualTo(topHashtagsSize);
        for (int i = 0; i < stats.TopHashtagsSize; i++)
        {
            assertThat(stats.getTopHashtags()[i]).isNull();
            assertThat(stats.getTopHashtagCounts()[0]).isZero();
        }

        assertThat(stats.getTotalHashtags()).isZero();
        assertThat(stats.getTotalTweets()).isZero();
        assertThat(stats.getTweetQueueCount()).isZero();
    }

    /**
     * Update top hashtags.
     */
    @Test
    void updateTopHashtags() {
    }

    /**
     * Gets daily tweets.
     */
    @Test
    void getDailyTweets() {
    }

    /**
     * Check that a top hashtags entry matches an expected hashtag and count.
     *
     * @param stats   the statistics instance to check
     * @param index   the index in top hashtag table
     * @param hashtag the expected hashtag
     * @param count   the expected count
     */
    private static void CheckTopHashtag(SampledStreamStats stats, int index, String hashtag, long count) {
        // Check the entry
        assertThat(stats.getTopHashtags()[index]).isEqualTo(hashtag);
        assertThat(stats.getTopHashtagCounts()[index]).isEqualTo(count);
    }

    /**
     * Create an instance of the SampledStreamStats class
     *
     * @param topHashtagsSize the size of the list for the top Hashtags
     * @return the new instance
     */
    private static SampledStreamStats CreateStatsInstance(int topHashtagsSize) {
        return new SampledStreamStats(topHashtagsSize);
    }
}
