package com.gobrs.async.core.common.exception;

/**
 * The type Duplicate method task exception.
 *
 * @program: gobrs -async
 * @ClassName DuplicateMethodTaskException
 * @description:
 * @author: sizegang
 * @create: 2023 -01-04
 */
public class DuplicateMethodTaskException extends RuntimeException {
    /**
     * Instantiates a new Duplicate method task exception.
     *
     * @param message the message
     */
    public DuplicateMethodTaskException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Duplicate method task exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DuplicateMethodTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Duplicate method task exception.
     */
    public DuplicateMethodTaskException() {
    }
}
