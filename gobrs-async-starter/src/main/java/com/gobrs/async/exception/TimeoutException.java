package com.gobrs.async.exception;

import com.gobrs.async.exception.GobrsAsyncException;

/**
 * @program: gobrs-async-starter
 * @ClassName TimeoutException
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public class TimeoutException extends GobrsAsyncException {

    private static final long serialVersionUID = -5423490721470482068L;

    public TimeoutException() {
        super();
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(Throwable cause) {
        super(cause);
    }

}
