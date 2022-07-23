package com.judcole.twitter.shared;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Class for background queues.
 *
 * @param <E> the type of each queue entry
 */
public class BackgroundQueue<E> implements IBackgroundQueue<E> {

    private final int QUEUE_SIZE = 100000;

    /**
     * The item queue.
     */
    private final ArrayBlockingQueue<E> items = new ArrayBlockingQueue<>(QUEUE_SIZE);

    /**
     * Try to remove and return the item at the beginning of the queue.
     *
     * @return the first item if found otherwise null
     */
    public E dequeue() {
        return items.poll();
    }

    /**
     * Schedule an item that needs to be processed.
     *
     * @param item the item
     */
    public void enqueue(E item) {
        if (item == null) throw new NumberFormatException("Attempt enqueue null item");

        try {
            items.put(item);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            throw new RuntimeException("Background Queue thread interrupted", ie);
        }
    }

    /**
     * Get the number of items currently in the queue.
     *
     * @return the count
     */
    public int getCount() {
        return items.size();
    }
}
