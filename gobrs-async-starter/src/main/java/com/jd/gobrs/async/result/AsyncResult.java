package com.jd.gobrs.async.result;

import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName AsyncResult
 * @description:
 * @author: sizegang
 * @create: 2022-02-26 14:47
 * @Version 1.0
 **/
@Data
public class AsyncResult<P> implements Serializable {

    private Map resultMap;
    GobrsAsyncSupport support;
    private Integer expCode;
}
