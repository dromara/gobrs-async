package com.gobrs.async.core.common.exception;

/**
 * The type Gobrs async com.gobrs.async.exception.
 *
 * @program: gobrs -async-starter
 * @ClassName GobrsAsyncException
 * @description:
 * @author: sizegang
 * @create: 2022 -03-16
 */
public class GobrsForceStopException extends RuntimeException {


    /**
     * Instantiates a new Gobrs async com.gobrs.async.exception.
     */
    public GobrsForceStopException() {
        super();
    }

    /**
     * Instantiates a new Gobrs async com.gobrs.async.exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public GobrsForceStopException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Gobrs async com.gobrs.async.exception.
     *
     * @param message the message
     */
    public GobrsForceStopException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Gobrs async com.gobrs.async.exception.
     *
     * @param cause the cause
     */
    public GobrsForceStopException(Throwable cause) {
        super(cause);
    }

}
