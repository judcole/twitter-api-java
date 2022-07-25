package com.judcole.twitter.shared;

/**
 * The interface for background queues.
 *
 * @param <E> the type of each queue entry
 */
public interface IBackgroundQueue<E> {

    /**
     * Try to remove and return the item at the beginning of the queue.
     *
     * @return the first item if found otherwise null
     */
    E dequeue();

    /**
     * Schedule an item that needs to be processed.
     *
     * @param item the item
     */
    void enqueue(E item);

    /**
     * Get the number of items currently in the queue.
     *
     * @return the count
     */
    int getCount();

    /**
     * Get the size of the queue.
     *
     * @return the size
     */
    int getSize();
}
