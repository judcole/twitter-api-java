package com.judcole.twitter.api;

import com.judcole.twitter.shared.BackgroundQueueFactory;
import com.judcole.twitter.shared.SampledStreamStatsFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * The class to configure the main application.
 */
@Configuration
@ComponentScan("com.judcole.twitter")
@EnableAsync
@Slf4j
public class Config implements AsyncConfigurer {
    // Shared background queue factory instance
    private final BackgroundQueueFactory sharedQueueFactory = new BackgroundQueueFactory();

    // Shared stream stats factory instance
    private final SampledStreamStatsFactory sharedStatsFactory = new SampledStreamStatsFactory();

    /**
     * Return the shared background queue factory.
     *
     * @return the background queue factory
     */
    @Bean
    public BackgroundQueueFactory queueFactory() {
        log.info("Returning the background queue factory");
        return sharedQueueFactory;
    }

    /**
     * Return the shared stream stats factory.
     *
     * @return the stream stats factory
     */
    @Bean
    public SampledStreamStatsFactory statsFactory() {
        log.info("Returning the sampled stream stats factory");
        return sharedStatsFactory;
    }

    /**
     * Configure and return the Async executor.
     *
     * @return the executor
    @ Bean(name = "threadPoolTaskExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("AsyncThread::");
        executor.initialize();
        return executor;
    }
*/
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("AsyncThread::");
        executor.initialize();
        log.info("Returning an async executor ");
        return executor;
    }
}
