package com.judcole.twitter.shared;

import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;

/**
 * The class to generate a shared and sized (singleton) statistics instance for the sampled stream.
 */
@Component
public class SampledStreamStatsFactory {

    // Default size of statistics table
    public static final int DEFAULT_STATS_SIZE = 10;

    // Shared total statistics
    private SampledStreamStats sharedStats = null;

    /**
     * Gets a shared statics instance with a table of a specified size.
     *
     * @param tableSize the table size
     * @return the stats instance
     */
    public SampledStreamStats getStatsInstance(int tableSize) {
        if (sharedStats == null) {
            // Allocate a new instance of the specified size
            sharedStats = new SampledStreamStats(tableSize);
        } else {
            // Check for a mismatch in the table size
            if (sharedStats.getTopHashtagsSize() != tableSize) {
                throw new InvalidParameterException("Mismatched statistics table size");
            }
        }

        // Return the singleton shared stats instance
        return sharedStats;
    }
}
