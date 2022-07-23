package com.judcole.twitter.shared;

/**
 * The class for the contents of a block of tweets from the Twitter stream.
 */
public class TweetBlock {
    /**
     * The contents of the block.
     */
    public final String Contents;

    /**
     * Instantiates a new tweet block.
     *
     * @param contents the contents
     */
    public TweetBlock(String contents) {
        Contents = contents;
    }
}
