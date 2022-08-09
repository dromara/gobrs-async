package com.gobrs.async.exception;

/**
 * The type Gobrs async exception.
 *
 * @program: gobrs -async-starter
 * @ClassName GobrsAsyncException
 * @description:
 * @author: sizegang
 * @create: 2022 -03-16
 */
public class GobrsAsyncException extends RuntimeException {

    private static final long serialVersionUID = 4898686611676070892L;

    /**
     * Instantiates a new Gobrs async exception.
     */
    public GobrsAsyncException() {
        super();
    }

    /**
     * Instantiates a new Gobrs async exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public GobrsAsyncException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Gobrs async exception.
     *
     * @param message the message
     */
    public GobrsAsyncException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Gobrs async exception.
     *
     * @param cause the cause
     */
    public GobrsAsyncException(Throwable cause) {
        super(cause);
    }

}
