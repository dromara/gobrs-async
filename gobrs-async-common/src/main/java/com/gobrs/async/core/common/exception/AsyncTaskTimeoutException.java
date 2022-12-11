package com.gobrs.async.core.common.exception;


/**
 * The type Timeout com.gobrs.async.exception.
 *
 * @program: gobrs -async-starter
 * @description:
 * @author: sizegang
 * @create: 2022 -03-16
 */
public class AsyncTaskTimeoutException extends GobrsAsyncException {

    private static final long serialVersionUID = -5423490721470482068L;

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     */
    public AsyncTaskTimeoutException() {
        super();
    }

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public AsyncTaskTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     *
     * @param message the message
     */
    public AsyncTaskTimeoutException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     *
     * @param cause the cause
     */
    public AsyncTaskTimeoutException(Throwable cause) {
        super(cause);
    }

}
