package com.jd.gobrs.async.example;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: gobrs-async
 * @ClassName DataContext
 * @description:
 * @author: sizegang
 * @create: 2022-03-05
 **/
public class DataContext {
    private HttpServletRequest httpServletRequest;

    private Object result;

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
