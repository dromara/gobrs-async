package com.gobrs.async.core.common.exception;

/**
 * The type Invoke method task exception.
 *
 * @program: gobrs -async
 * @ClassName InvokeMethodTaskException
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public class InvokeMethodTaskException extends RuntimeException{

    /**
     * Instantiates a new Invoke method task exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public InvokeMethodTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Invoke method task exception.
     *
     * @param cause the cause
     */
    public InvokeMethodTaskException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Invoke method task exception.
     *
     * @param message the message
     */
    public InvokeMethodTaskException(String message) {
        super(message);
    }
}
