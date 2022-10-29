package com.gobrs.async.core.common.exception;


/**
 * The type Timeout com.gobrs.async.exception.
 *
 * @program: gobrs -async-starter
 * @ClassName TimeoutException
 * @description:
 * @author: sizegang
 * @create: 2022 -03-16
 */
public class TimeoutException extends GobrsAsyncException {

    private static final long serialVersionUID = -5423490721470482068L;

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     */
    public TimeoutException() {
        super();
    }

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     *
     * @param message the message
     */
    public TimeoutException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Timeout com.gobrs.async.exception.
     *
     * @param cause the cause
     */
    public TimeoutException(Throwable cause) {
        super(cause);
    }

}
