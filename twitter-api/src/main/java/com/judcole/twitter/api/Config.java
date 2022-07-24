package com.judcole.twitter.api;

import com.judcole.twitter.shared.SampledStreamStats;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The class to configure the main application.
 */
@Configuration
@ComponentScan("com.judcole.twitter")
public class Config {
    // Default size of statistics table
    static final int STATS_SIZE = 10;

    @Bean
    public SampledStreamStats stats() {
        return new SampledStreamStats(STATS_SIZE);
    }
}
