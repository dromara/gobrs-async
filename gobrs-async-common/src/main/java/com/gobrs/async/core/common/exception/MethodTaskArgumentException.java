package com.gobrs.async.core.common.exception;

/**
 * The type Method task argument exception.
 *
 * @program: gobrs -async
 * @ClassName MethodTaskArgumentException
 * @description:
 * @author: sizegang
 * @create: 2023 -01-05
 */
public class MethodTaskArgumentException extends RuntimeException{

    /**
     * Instantiates a new Method task argument exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public MethodTaskArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Method task argument exception.
     *
     * @param cause the cause
     */
    public MethodTaskArgumentException(Throwable cause) {
        super(cause);
    }
}
