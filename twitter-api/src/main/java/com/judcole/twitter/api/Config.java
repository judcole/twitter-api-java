package com.judcole.twitter.api;

import com.judcole.twitter.shared.SampledStreamStatsFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The class to configure the main application.
 */
@Configuration
@ComponentScan("com.judcole.twitter")
public class Config {
    @Bean
    public SampledStreamStatsFactory statsFactory() {
        return new SampledStreamStatsFactory();
    }
}
