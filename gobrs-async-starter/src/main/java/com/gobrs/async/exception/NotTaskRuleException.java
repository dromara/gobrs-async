package com.gobrs.async.exception;

/**
 * @program: gobrs-async
 * @ClassName NotTaskRuleException
 * @description:
 * @author: sizegang
 * @create: 2022-01-27
 * @Version 1.0
 **/
public class NotTaskRuleException extends RuntimeException {
    public NotTaskRuleException() {
        super();
    }

    public NotTaskRuleException(String message) {
        super(message);
    }

}
