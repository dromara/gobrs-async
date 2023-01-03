package com.gobrs.async.core.common.domain;


import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Async result.
 *
 * @program: gobrs -async-starter
 * @ClassName AsyncResult 流程执行结果封装
 * @description: Process results
 * @author: sizegang
 * @create: 2022 -03-19
 */
@Data
public class AsyncResult implements Serializable {

    /**
     * 整流程 执行结果code
     */
    private Integer executeCode;

    private Integer cusCode;

    /**
     * 整流程 执行是否成功
     */
    private boolean status;

    /**
     * 执行结果封装
     * key com.gobrs.async.test.task 类
     * value 执行结果 （单任务）
     */
    private Map<Class, TaskResult> resultMap = new HashMap();
}
