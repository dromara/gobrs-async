package com.gobrs.async.core.common.exception;

/**
 * The type Method task not found exception.
 *
 * @program: gobrs -async
 * @ClassName MethodTaskNotFoundException
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public class MethodTaskNotFoundException extends RuntimeException{
    /**
     * Instantiates a new Method task not found exception.
     *
     * @param message the message
     */
    public MethodTaskNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Method task not found exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public MethodTaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


}
