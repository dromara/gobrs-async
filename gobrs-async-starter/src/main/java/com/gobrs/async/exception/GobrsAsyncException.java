package com.gobrs.async.exception;

/**
 * @program: gobrs-async-starter
 * @ClassName GobrsAsyncException
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public class GobrsAsyncException extends RuntimeException {

    private static final long serialVersionUID = 4898686611676070892L;

    public GobrsAsyncException() {
        super();
    }

    public GobrsAsyncException(String message, Throwable cause) {
        super(message, cause);
    }

    public GobrsAsyncException(String message) {
        super(message);
    }

    public GobrsAsyncException(Throwable cause) {
        super(cause);
    }

}
