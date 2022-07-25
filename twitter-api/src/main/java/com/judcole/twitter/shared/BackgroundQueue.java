package com.judcole.twitter.shared;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Class for background queues.
 *
 * @param <E> the type of each queue entry
 */
public class BackgroundQueue<E> implements IBackgroundQueue<E> {

    /**
     * The item queue.
     */
    private final ArrayBlockingQueue<E> items;

    /**
     * Instantiates a new Background queue.
     *
     * @param queueSize the queue size
     */
    public BackgroundQueue(int queueSize) {
        items = new ArrayBlockingQueue<>(queueSize);
    }

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

    /**
     * Get the size of the queue.
     *
     * @return the size
     */
    public int getSize() {
        return items.size() + items.remainingCapacity();
    }
}
