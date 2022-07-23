package com.judcole.twitter.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Class to test BackgroundQueues.
 */
class BackgroundQueueTests {

    @Test
    void getCount() {
        var queue = new BackgroundQueue<Integer>();
        assertThat(queue.getCount()).isEqualTo(0);
        queue.enqueue(1);
        assertThat(queue.getCount()).isEqualTo(1);
        queue.enqueue(2);
        assertThat(queue.getCount()).isEqualTo(2);
    }

    @Test
    void enqueue() {
        var queue = new BackgroundQueue<Integer>();
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);
        assertThat(queue.getCount()).isEqualTo(3);
    }

    @Test
    void dequeue() {
        final int VALUE1 = 100;
        final int VALUE2 = 200;
        var queue = new BackgroundQueue<Integer>();
        var item = queue.dequeue();
        assertThat(item).isNull();
        queue.enqueue(VALUE1);
        item = queue.dequeue();
        assertThat(item).isEqualTo(VALUE1);
        queue.enqueue(VALUE1);
        queue.enqueue(VALUE2);
        assertThat(queue.getCount()).isEqualTo(2);
        item = queue.dequeue();
        assertThat(item).isEqualTo(VALUE1);
        assertThat(queue.getCount()).isEqualTo(1);
        item = queue.dequeue();
        assertThat(item).isEqualTo(VALUE2);
        assertThat(queue.getCount()).isEqualTo(0);
    }
}