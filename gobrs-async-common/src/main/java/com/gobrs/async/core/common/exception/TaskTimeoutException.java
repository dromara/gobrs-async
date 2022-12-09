package com.gobrs.async.core.common.exception;

/**
 * The type Task timeout exception.
 *
 * @program: gobrs -async
 * @ClassName TaskTimeoutException
 * @description:
 * @author: sizegang
 * @create: 2022 -12-09
 */
public class TaskTimeoutException extends GobrsAsyncException {

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     */
    public TaskTimeoutException() {
        super();
    }

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public TaskTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     *
     * @param message the message
     */
    public TaskTimeoutException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     *
     * @param cause the cause
     */
    public TaskTimeoutException(Throwable cause) {
        super(cause);
    }
}
