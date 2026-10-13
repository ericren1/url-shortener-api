package com.ericren.urlshortener.exception;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException() {
        super("Too many URL creation requests. Try again later.");
    }
}