package com.gobrs.async.core.task;


import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.callback.ErrorCallback;
import com.gobrs.async.core.common.enums.ExpState;
import com.gobrs.async.core.common.util.SystemClock;
import com.gobrs.async.core.config.ConfigManager;
import com.gobrs.async.core.log.LogTracer;
import com.gobrs.async.core.log.LogWrapper;
import com.gobrs.async.core.log.TraceUtil;
import com.gobrs.async.core.common.def.DefaultConfig;
import com.gobrs.async.core.common.domain.AnyConditionResult;
import com.gobrs.async.core.common.domain.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Async com.gobrs.async.com.gobrs.async.test.task.
 * 每一个任务需要继承此 抽象类
 *
 * @param <Param>  the type parameter
 * @param <Result> the type parameter
 * @program: gobrs -async-starter
 * @ClassName AsyncTask
 * @description:
 * @author: sizegang
 * @create: 2022 -03-16
 */
public abstract class AsyncTask<Param, Result> implements GobrsTask<Param, Result> {

    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(AsyncTask.class);

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务描述
     */
    private String desc;


    /**
     * Transaction com.gobrs.async.com.gobrs.async.test.task
     */
    private boolean callback = DefaultConfig.transaction;

    private int retryCount = DefaultConfig.retryCount;

    /**
     * Whether to execute a subtask if it fails
     */
    private boolean failSubExec = DefaultConfig.failSubExec;

    /**
     * if true => execute when any of parentTasks finished
     */

    private boolean any = false;

    private boolean anyCondition = false;

    /**
     * Whether any interruption ends other dependent tasks
     */
    private boolean exclusive = false;

    /**
     * get result of current com.gobrs.async.com.gobrs.async.test.task
     *
     * @param support the support
     * @return result result
     */
    public Result getResult(TaskSupport support) {
        return getResult(support, false);
    }

    /**
     * Gets result.
     *
     * @param support      the support
     * @param anyCondition the any condition
     * @return the result
     */
    public Result getResult(TaskSupport support, boolean anyCondition) {
        TaskResult<Result> taskResult = getTaskResult(support);
        if (anyCondition == true && taskResult != null) {
            return (Result) AnyConditionResult.builder().setResult(taskResult.getResult()).build();
        }
        if (taskResult != null) {
            return taskResult.getResult();
        }
        return null;
    }


    /**
     * Task adapter result.
     *
     * @param param   the param
     * @param support the support
     * @return the result
     */
    public Result taskAdapter(Param param, TaskSupport support) {
        Long startTime = SystemClock.now();
        Result task;
        try {
            task = task(param, support);
        } catch (Exception exception) {
            throw exception;
        } finally {
            boolean costLogabled = ConfigManager.Action.costLogabled(support.getRuleName());
            if (costLogabled &&
                    Objects.nonNull(support.getLogWrapper())) {
                long costTime = SystemClock.now() - startTime;
                LogTracer logTracer = LogTracer.builder()
                        .taskName(this.getName())
                        .taskCost(costTime)
                        .build();
                LogWrapper logWrapper = support.getLogWrapper();
                logWrapper.addTrace(logTracer);
                logWrapper.setProcessCost(costTime);
            }
        }
        return task;
    }


    /**
     * Tasks to be performed
     *
     * @param param   the param
     * @param support the support
     * @return result result
     */
    public abstract Result task(Param param, TaskSupport support);

    /**
     * On failure trace.
     * 执行失败 回调
     *
     * @param support   the support
     * @param exception the com.gobrs.async.exception
     */
    public void onFailureTrace(TaskSupport support, Exception exception) {
        boolean logable = ConfigManager.Action.errLogabled(support.getRuleName());
        if (logable) {
            logger.error("[traceId:{}] {} 任务执行失败", TraceUtil.get(), this.getName(), exception);
        }
        onFail(support, exception);
    }

    /**
     * get result of depend on class
     *
     * @param <Result> the type parameter
     * @param support  the support
     * @param clazz    depend on class
     * @param type     the type
     * @return result result
     */
    public <Result> Result getResult(TaskSupport support, Class<? extends Task> clazz, Class<Result> type) {
        TaskResult<Result> taskResult = getTaskResult(support, clazz, type);
        if (taskResult != null) {
            return taskResult.getResult();
        }
        return null;
    }


    /**
     * get taskResult of current com.gobrs.async.com.gobrs.async.test.task
     *
     * @param support the support
     * @return com.gobrs.async.com.gobrs.async.test.task result
     */
    public TaskResult<Result> getTaskResult(TaskSupport support) {
        Map<Class, TaskResult> resultMap = support.getResultMap();
        Class thisResultClass = this.getClass();
        return resultMap.get(thisResultClass) != null ? resultMap.get(thisResultClass) : resultMap.get(depKey(thisResultClass));
    }

    /**
     * get taskResult of depend on class
     *
     * @param <Result> TaskResult<R>
     * @param support  the support
     * @param clazz    depend on class
     * @param type     the type
     * @return com.gobrs.async.com.gobrs.async.test.task result
     */
    public <Result> TaskResult<Result> getTaskResult(TaskSupport support, Class<? extends Task> clazz, Class<Result> type) {
        Map<Class, TaskResult> resultMap = support.getResultMap();
        return resultMap.get(clazz) != null ? resultMap.get(clazz) : resultMap.get(depKey(clazz));
    }

    /**
     * get com.gobrs.async.com.gobrs.async.test.task param
     *
     * @param support the support
     * @return param param
     */
    public Param getParam(TaskSupport support) {
        Object taskResult = support.getParam();
        if (taskResult != null) {
            return (Param) taskResult;
        }
        return null;
    }

    /**
     * Dep key string.
     *
     * @param clazz the clazz
     * @return the string
     */
    String depKey(Class clazz) {
        char[] cs = clazz.getSimpleName().toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

    /**
     * Stop async boolean.
     * 主动中断任务流程 API调用
     *
     * @param support the support
     * @return the boolean
     */
    public boolean stopAsync(TaskSupport support) {
        try {
            ErrorCallback errorCallback = new ErrorCallback(() -> support.getParam(), null, support, this);
            support.taskLoader.setExpCode(new AtomicInteger(ExpState.DEFAULT.getCode()));
            support.taskLoader.errorInterrupted(errorCallback);
        } catch (Exception ex) {
            logger.error("stopAsync error {}", ex);
            return false;
        }
        return true;
    }

    /**
     * Stop async boolean.
     *
     * @param support the support
     * @param expCode the exp code
     * @return the boolean
     */
    public boolean stopAsync(TaskSupport support, Integer expCode) {
        try {
            support.taskLoader.setIsRunning(false);
            support.taskLoader.setExpCode(new AtomicInteger(expCode));

            ErrorCallback errorCallback = new ErrorCallback(() -> support.getParam(), null, support, this);
            support.taskLoader.errorInterrupted(errorCallback);

        } catch (Exception ex) {
            logger.error("stopAsync error {} ", ex);
            return false;
        }
        return true;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Is com.gobrs.async.callback boolean.
     *
     * @return the boolean
     */
    public boolean isCallback() {
        return callback;
    }

    /**
     * Sets com.gobrs.async.callback.
     *
     * @param callback the com.gobrs.async.callback
     */
    public void setCallback(boolean callback) {
        this.callback = callback;
    }


    /**
     * Gets retry count.
     *
     * @return the retry count
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * Sets retry count.
     *
     * @param retryCount the retry count
     */
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * Is fail sub exec boolean.
     *
     * @return the boolean
     */
    public boolean isFailSubExec() {
        return failSubExec;
    }

    /**
     * Sets fail sub exec.
     *
     * @param failSubExec the fail sub exec
     */
    public void setFailSubExec(boolean failSubExec) {
        this.failSubExec = failSubExec;
    }


    /**
     * Is any boolean.
     *
     * @return the boolean
     */
    public boolean isAny() {
        return any;
    }

    /**
     * Sets any.
     *
     * @param any the any
     */
    public void setAny(boolean any) {
        this.any = any;
    }

    /**
     * Is exclusive boolean.
     *
     * @return the boolean
     */
    public boolean isExclusive() {
        return exclusive;
    }

    /**
     * Sets exclusive.
     *
     * @param exclusive the exclusive
     */
    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }


    /**
     * Is any condition boolean.
     *
     * @return the boolean
     */
    public boolean isAnyCondition() {
        return anyCondition;
    }

    /**
     * Sets any condition.
     *
     * @param anyCondition the any condition
     */
    public void setAnyCondition(boolean anyCondition) {
        this.anyCondition = anyCondition;
    }

}
