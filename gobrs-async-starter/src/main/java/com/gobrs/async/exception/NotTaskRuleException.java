package com.gobrs.async.exception;

/**
 * The type Not task rule exception.
 *
 * @program: gobrs -async
 * @ClassName NotTaskRuleException
 * @description:
 * @author: sizegang
 * @create: 2022 -01-27
 * @Version 1.0
 */
public class NotTaskRuleException extends RuntimeException {
    /**
     * Instantiates a new Not task rule exception.
     */
    public NotTaskRuleException() {
        super();
    }

    /**
     * Instantiates a new Not task rule exception.
     *
     * @param message the message
     */
    public NotTaskRuleException(String message) {
        super(message);
    }

}
