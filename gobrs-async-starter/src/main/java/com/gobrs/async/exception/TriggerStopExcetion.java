package com.gobrs.async.exception;

/**
 * Manually trigger stop exception
 */
public class TriggerStopExcetion extends RuntimeException {
    public TriggerStopExcetion() {
        super();
    }

    public TriggerStopExcetion(String message) {
        super(message);
    }
}
