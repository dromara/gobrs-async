package com.gobrs.async.core.common.exception;

/**
 * The type Gobrs config exception.
 *
 * @program: gobrs -async
 * @ClassName GobrsConfigException
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public class GobrsConfigException extends RuntimeException{

    /**
     * Instantiates a new Gobrs config exception.
     *
     * @param message the message
     */
    public GobrsConfigException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Gobrs config exception.
     *
     * @param cause the cause
     */
    public GobrsConfigException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Gobrs config exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public GobrsConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Gobrs config exception.
     */
    public GobrsConfigException() {
    }
}
