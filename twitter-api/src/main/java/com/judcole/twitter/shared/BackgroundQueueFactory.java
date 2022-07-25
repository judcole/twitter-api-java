package com.judcole.twitter.shared;

import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;

/**
 * The class to generate a shared (singleton) background queue instance for the sampled stream.
 */
@Component
public class BackgroundQueueFactory {
    // Default size of queue
    public static final int DEFAULT_QUEUE_SIZE = 100000;

    // Shared background queue
    private BackgroundQueue<TweetBlock> sharedQueue = null;

    /**
     * Gets a shared background queue instance of a specified size.
     *
     * @param queueSize the queue size
     * @return the queue instance
     */
    public BackgroundQueue<TweetBlock> getBackgroundQueueInstance(int queueSize) {
        if (sharedQueue == null) {
            // Allocate a new instance of the specified size
            sharedQueue = new BackgroundQueue<>(queueSize);
        } else {
            // Check for a mismatch in the table size
            if (sharedQueue.getSize() != queueSize) {
                throw new InvalidParameterException("Mismatched background queue size");
            }
        }

        // Return the singleton shared background queue instance
        return sharedQueue;
    }
}
