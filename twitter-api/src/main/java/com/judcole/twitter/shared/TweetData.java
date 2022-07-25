package com.judcole.twitter.shared;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The class for the data for an incoming tweet.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TweetData {
    /**
     * The tweet Id.
     */
    public String id;
    /**
     * The tweet text.
     */
    public String text;
}
