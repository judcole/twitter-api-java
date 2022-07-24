package com.judcole.twitter.api;

import com.judcole.twitter.shared.SampledStreamStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.time.LocalDateTime.now;

/**
 * The class for the API controller to get the latest statistics.
 */
@RestController
@Slf4j
public class SampledStreamController {

    // Application start time
    private static final LocalDateTime startTime = now(ZoneOffset.UTC);

    // Shared total statistics
    private final SampledStreamStats stats;

    /**
     * Instantiates a new Sampled stream controller.
     *
     * @param sharedStats the shared total statistics
     */
    public SampledStreamController(SampledStreamStats sharedStats) {
        // Save the shared stats parameter
        stats = sharedStats;
    }

    /**
     * Get the latest statistics.
     *
     * @param response the HTTP response object
     * @return the statistics object containing the latest stats
     */
    @GetMapping(value = "/getStats")
    public SampledStreamStats getStats(HttpServletResponse response) {

        // Log the call
        log.info("Returning a tweet count of {}", stats.getTotalTweets());

        // Allow access from localhost UI application to avoid browser CORS errors
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:5000");
        response.addHeader("Access-Control-Allow-Methods", "GET");

        // Get the latest statistics data
        var stats = GetSampledStreamStats();

        if (stats == null) {
            // Something went wrong so return a Not Found status code
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        // Return the statistics data
        return stats;
    }

    /**
     * Get the latest statistics data.
     *
     * @return the latest statistics data
     */
    private SampledStreamStats GetSampledStreamStats() {
        // Calculate and set all calculated fields
        stats.SetCalculatedFields(startTime);

        // Return the stats data
        return stats;
    }
}
