package com.gobrs.async.core.common.exception;

/**
 * The type Gobrs async com.gobrs.async.exception.
 *
 * @program: gobrs -async-starter
 * @description:
 * @author: sizegang
 * @create: 2022 -03-16
 */
public class ManualStopException extends RuntimeException {


    /**
     * Instantiates a new Gobrs async com.gobrs.async.exception.
     */
    public ManualStopException() {
        super();
    }

    /**
     * Instantiates a new Gobrs async com.gobrs.async.exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ManualStopException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Gobrs async com.gobrs.async.exception.
     *
     * @param message the message
     */
    public ManualStopException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Gobrs async com.gobrs.async.exception.
     *
     * @param cause the cause
     */
    public ManualStopException(Throwable cause) {
        super(cause);
    }

}
