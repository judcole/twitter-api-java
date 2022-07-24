package com.judcole.twitter.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Class for testing the SampleStreamStats class.
 */
class SampledStreamStatsTests {

    // Some test values
    private final String HASHTAG1 = "abc";
    private final String HASHTAG2 = "abc1";
    private final String HASHTAG3 = "abc12";
    private final String HASHTAG4 = "xYz";
    private final String HASHTAG5 = "aLongHashTag";

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

    /**
     * Test that the creation of a new SampledStreamStats object is successful.
     *
     * @param topHashtagsSize the size of the list for the top Hashtags
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 3, 9, 100})
    void SampledStreamStats_Create_ReturnInstance(int topHashtagsSize) {
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

        assertThat(stats.getTopHashtagsSize()).isEqualTo(topHashtagsSize);
        for (int i = 0; i < stats.getTopHashtagsSize(); i++) {
            assertThat(stats.getTopHashtags()[i]).isNull();
            assertThat(stats.getTopHashtagCounts()[0]).isZero();
        }

        assertThat(stats.getTotalHashtags()).isZero();
        assertThat(stats.getTotalTweets()).isZero();
        assertThat(stats.getTweetQueueCount()).isZero();
    }

    /**
     * Test that setting the Status is successful.
     *
     * @param status the value for the status
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "Good", "A very bad status"})
    void SampledStreamStats_SetStatus_Successful(String status) {
        // Create an instance
        var stats = CreateStatsInstance(1);

        // Set the status to a null
        stats.setStatus(null);

        // Check the result
        assertThat(stats.getStatus()).isNull();

        // Set the status
        stats.setStatus(status);

        // Check the result
        assertThat(stats.getStatus()).isEqualTo(status);
    }

    /**
     * Test that hashtags and their counts are properly added to a list of 2 top hashtags.
     */
    @ParameterizedTest
    @ValueSource(strings = {"aBcDeF", "xYz123", "XXXx", "zZZZ"})
    public void UpdateTopHashtags_AddSameTagDifferentCase_Successful(String hashtag) {
        // Create an instance with two top hashtags
        var stats = CreateStatsInstance(2);

        // Add a mixed case hashtag and check the result
        stats.UpdateTopHashtags(hashtag, 10);
        CheckTopHashtag(stats, 0, hashtag, 10);
        CheckTopHashtag(stats, 1, null, 0);

        // Add the same hashtag lower-cased with lower count and check the result
        final String hashtag_lower = hashtag.toLowerCase();
        stats.UpdateTopHashtags(hashtag_lower, 9);
        CheckTopHashtag(stats, 0, hashtag_lower, 9);
        CheckTopHashtag(stats, 1, null, 0);

        // Restore the mixed case hashtag and check the result
        stats.UpdateTopHashtags(hashtag, 10);
        CheckTopHashtag(stats, 0, hashtag, 10);
        CheckTopHashtag(stats, 1, null, 0);

        // Add the same hashtag lower-cased with the same count and check the result
        stats.UpdateTopHashtags(hashtag_lower, 10);
        CheckTopHashtag(stats, 0, hashtag_lower, 10);
        CheckTopHashtag(stats, 1, null, 0);

        // Restore the mixed case hashtag and check the result
        stats.UpdateTopHashtags(hashtag, 10);
        CheckTopHashtag(stats, 0, hashtag, 10);
        CheckTopHashtag(stats, 1, null, 0);

        // Add the same hashtag lower-cased with higher count and check the result
        stats.UpdateTopHashtags(hashtag_lower, 11);
        CheckTopHashtag(stats, 0, hashtag_lower, 11);
        CheckTopHashtag(stats, 1, null, 0);

        // Add the same hashtag upper-cased with lower count and check the result
        final String hashtag_upper = hashtag.toUpperCase();
        stats.UpdateTopHashtags(hashtag_upper, 9);
        CheckTopHashtag(stats, 0, hashtag_upper, 9);
        CheckTopHashtag(stats, 1, null, 0);

        // Restore the mixed case hashtag and check the result
        stats.UpdateTopHashtags(hashtag, 10);
        CheckTopHashtag(stats, 0, hashtag, 10);
        CheckTopHashtag(stats, 1, null, 0);

        // Add the same hashtag upper-cased with the same count and check the result
        stats.UpdateTopHashtags(hashtag_upper, 10);
        CheckTopHashtag(stats, 0, hashtag_upper, 10);
        CheckTopHashtag(stats, 1, null, 0);

        // Restore the mixed case hashtag and check the result
        stats.UpdateTopHashtags(hashtag, 10);
        CheckTopHashtag(stats, 0, hashtag, 10);
        CheckTopHashtag(stats, 1, null, 0);

        // Add the same hashtag lower cased with higher count and check the result
        stats.UpdateTopHashtags(hashtag_upper, 11);
        CheckTopHashtag(stats, 0, hashtag_upper, 11);
        CheckTopHashtag(stats, 1, null, 0);

        // Add a different hashtag with a lower count and check the result
        stats.UpdateTopHashtags(HASHTAG1, 1);
        CheckTopHashtag(stats, 0, hashtag_upper, 11);
        CheckTopHashtag(stats, 1, HASHTAG1, 1);

        // Add a different hashtag with the same count and check the result
        stats.UpdateTopHashtags(HASHTAG1, 11);
        CheckTopHashtag(stats, 0, HASHTAG1, 11);
        CheckTopHashtag(stats, 1, hashtag_upper, 11);

        // Add a different hashtag with a higher count and check the result
        stats.UpdateTopHashtags(HASHTAG1, 12);
        CheckTopHashtag(stats, 0, HASHTAG1, 12);
        CheckTopHashtag(stats, 1, hashtag_upper, 11);
    }

    /**
     * Test that hashtags and their counts are properly added to a list of 1 top hashtags.
     */
    @Test
    public void UpdateTopHashtags_AddTo1Top_Successful() {
        // Create an instance with one top hashtag
        var stats = CreateStatsInstance(1);

        // Add a hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG1, 1);
        CheckTopHashtag(stats, 0, HASHTAG1, 1);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG1, 2);
        CheckTopHashtag(stats, 0, HASHTAG1, 2);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG1, 10);
        CheckTopHashtag(stats, 0, HASHTAG1, 10);

        // Add another hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG2, 9);
        CheckTopHashtag(stats, 0, HASHTAG1, 10);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG2, 10);
        CheckTopHashtag(stats, 0, HASHTAG2, 10);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG2, 11);
        CheckTopHashtag(stats, 0, HASHTAG2, 11);

        // Add another hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG3, 10);
        CheckTopHashtag(stats, 0, HASHTAG2, 11);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG3, 20);
        CheckTopHashtag(stats, 0, HASHTAG3, 20);
    }

    /**
     * Test that hashtags and their counts are properly added to a list of 2 top hashtags.
     */
    @Test
    public void UpdateTopHashtags_AddTo2Top_Successful() {
        // Create an instance with two top hashtags
        var stats = CreateStatsInstance(2);

        // Add a hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG1, 1);
        CheckTopHashtag(stats, 0, HASHTAG1, 1);
        CheckTopHashtag(stats, 1, null, 0);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG1, 2);
        CheckTopHashtag(stats, 0, HASHTAG1, 2);
        CheckTopHashtag(stats, 1, null, 0);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG1, 10);
        CheckTopHashtag(stats, 0, HASHTAG1, 10);
        CheckTopHashtag(stats, 1, null, 0);

        // Add another hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG2, 9);
        CheckTopHashtag(stats, 0, HASHTAG1, 10);
        CheckTopHashtag(stats, 1, HASHTAG2, 9);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG2, 10);
        CheckTopHashtag(stats, 0, HASHTAG2, 10);
        CheckTopHashtag(stats, 1, HASHTAG1, 10);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG2, 11);
        CheckTopHashtag(stats, 0, HASHTAG2, 11);
        CheckTopHashtag(stats, 1, HASHTAG1, 10);

        // Add the first one again and check the result
        stats.UpdateTopHashtags(HASHTAG1, 12);
        CheckTopHashtag(stats, 0, HASHTAG1, 12);
        CheckTopHashtag(stats, 1, HASHTAG2, 11);
    }

    /**
     * Test that hashtags and their counts are properly added to a list of 3 top hashtags.
     */
    @Test
    public void UpdateTopHashtags_AddTo3Top_Successful() {
        // Create an instance with 3 top hashtags
        var stats = CreateStatsInstance(3);

        // Add a hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG1, 1);
        CheckTopHashtag(stats, 0, HASHTAG1, 1);
        CheckTopHashtag(stats, 1, null, 0);
        CheckTopHashtag(stats, 2, null, 0);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG1, 2);
        CheckTopHashtag(stats, 0, HASHTAG1, 2);
        CheckTopHashtag(stats, 1, null, 0);
        CheckTopHashtag(stats, 2, null, 0);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG1, 10);
        CheckTopHashtag(stats, 0, HASHTAG1, 10);
        CheckTopHashtag(stats, 1, null, 0);
        CheckTopHashtag(stats, 2, null, 0);

        // Add another hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG2, 9);
        CheckTopHashtag(stats, 0, HASHTAG1, 10);
        CheckTopHashtag(stats, 1, HASHTAG2, 9);
        CheckTopHashtag(stats, 2, null, 0);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG2, 10);
        CheckTopHashtag(stats, 0, HASHTAG2, 10);
        CheckTopHashtag(stats, 1, HASHTAG1, 10);
        CheckTopHashtag(stats, 2, null, 0);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG2, 11);
        CheckTopHashtag(stats, 0, HASHTAG2, 11);
        CheckTopHashtag(stats, 1, HASHTAG1, 10);
        CheckTopHashtag(stats, 2, null, 0);

        // Add another hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG3, 9);
        CheckTopHashtag(stats, 0, HASHTAG2, 11);
        CheckTopHashtag(stats, 1, HASHTAG1, 10);
        CheckTopHashtag(stats, 2, HASHTAG3, 9);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG3, 10);
        CheckTopHashtag(stats, 0, HASHTAG2, 11);
        CheckTopHashtag(stats, 1, HASHTAG3, 10);
        CheckTopHashtag(stats, 2, HASHTAG1, 10);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG3, 11);
        CheckTopHashtag(stats, 0, HASHTAG3, 11);
        CheckTopHashtag(stats, 1, HASHTAG2, 11);
        CheckTopHashtag(stats, 2, HASHTAG1, 10);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG3, 12);
        CheckTopHashtag(stats, 0, HASHTAG3, 12);
        CheckTopHashtag(stats, 1, HASHTAG2, 11);
        CheckTopHashtag(stats, 2, HASHTAG1, 10);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG4, 12);
        CheckTopHashtag(stats, 0, HASHTAG4, 12);
        CheckTopHashtag(stats, 1, HASHTAG3, 12);
        CheckTopHashtag(stats, 2, HASHTAG2, 11);

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG5, 12);
        CheckTopHashtag(stats, 0, HASHTAG5, 12);
        CheckTopHashtag(stats, 1, HASHTAG4, 12);
        CheckTopHashtag(stats, 2, HASHTAG3, 12);
    }

    /**
     * Test that hashtags and their counts are properly added to a list of 9 top hashtags.
     */
    @Test
    public void UpdateTopHashtags_AddTo9Top_Successful() {
        // Create an instance with 9 top hashtags
        final int LIST_SIZE = 9;
        var stats = CreateStatsInstance(LIST_SIZE);

        // Add a hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG1, 1);
        CheckTopHashtag(stats, 0, HASHTAG1, 1);
        for (int i = 1; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG1, 2);
        CheckTopHashtag(stats, 0, HASHTAG1, 2);
        for (int i = 1; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG1, 10);
        CheckTopHashtag(stats, 0, HASHTAG1, 10);
        for (int i = 1; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add another hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG2, 9);
        CheckTopHashtag(stats, 0, HASHTAG1, 10);
        CheckTopHashtag(stats, 1, HASHTAG2, 9);
        for (int i = 2; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG2, 10);
        CheckTopHashtag(stats, 0, HASHTAG2, 10);
        CheckTopHashtag(stats, 1, HASHTAG1, 10);
        for (int i = 2; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG2, 11);
        CheckTopHashtag(stats, 0, HASHTAG2, 11);
        CheckTopHashtag(stats, 1, HASHTAG1, 10);
        for (int i = 2; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add another hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG3, 9);
        CheckTopHashtag(stats, 0, HASHTAG2, 11);
        CheckTopHashtag(stats, 1, HASHTAG1, 10);
        CheckTopHashtag(stats, 2, HASHTAG3, 9);
        for (int i = 3; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG3, 10);
        CheckTopHashtag(stats, 0, HASHTAG2, 11);
        CheckTopHashtag(stats, 1, HASHTAG3, 10);
        CheckTopHashtag(stats, 2, HASHTAG1, 10);
        for (int i = 3; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG3, 11);
        CheckTopHashtag(stats, 0, HASHTAG3, 11);
        CheckTopHashtag(stats, 1, HASHTAG2, 11);
        CheckTopHashtag(stats, 2, HASHTAG1, 10);
        for (int i = 3; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add it again and check the result
        stats.UpdateTopHashtags(HASHTAG3, 12);
        CheckTopHashtag(stats, 0, HASHTAG3, 12);
        CheckTopHashtag(stats, 1, HASHTAG2, 11);
        CheckTopHashtag(stats, 2, HASHTAG1, 10);
        for (int i = 3; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add another hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG4, 11);
        CheckTopHashtag(stats, 0, HASHTAG3, 12);
        CheckTopHashtag(stats, 1, HASHTAG4, 11);
        CheckTopHashtag(stats, 2, HASHTAG2, 11);
        CheckTopHashtag(stats, 3, HASHTAG1, 10);
        for (int i = 4; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }

        // Add another hashtag and check the result
        stats.UpdateTopHashtags(HASHTAG5, 12);
        CheckTopHashtag(stats, 0, HASHTAG5, 12);
        CheckTopHashtag(stats, 1, HASHTAG3, 12);
        CheckTopHashtag(stats, 2, HASHTAG4, 11);
        CheckTopHashtag(stats, 3, HASHTAG2, 11);
        CheckTopHashtag(stats, 4, HASHTAG1, 10);
        for (int i = 5; i < LIST_SIZE; i++) {
            CheckTopHashtag(stats, i, null, 0);
        }
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
