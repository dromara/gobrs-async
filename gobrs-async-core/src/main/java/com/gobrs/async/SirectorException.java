package com.gobrs.async;

/**
 * @program: gobrs-async-starter
 * @ClassName SirectorException
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public class SirectorException extends RuntimeException {

    private static final long serialVersionUID = 4898686611676070892L;

    public SirectorException() {
        super();
    }

    public SirectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SirectorException(String message) {
        super(message);
    }

    public SirectorException(Throwable cause) {
        super(cause);
    }

}
