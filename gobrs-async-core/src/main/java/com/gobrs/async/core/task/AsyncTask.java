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
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gobrs.async.core.common.util.ExceptionUtil.exceptionInterceptor;

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

    private int retryCount = DefaultConfig.RETRY_COUNT;

    /**
     * Whether to execute a subtask if it fails
     */
    private boolean failSubExec = DefaultConfig.failSubExec;

    private int timeoutInMilliseconds = DefaultConfig.TASK_TIME_OUT;

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
        Exception exeError = null;
        try {
            task = task(param, support);
        } catch (Exception exception) {
            exeError = exception;
            throw exception;
            //  InterruptedException exceptions do nothing
        } finally {
            boolean costLogabled = ConfigManager.Action.costLogabled(support.getRuleName());
            if (costLogabled &&
                    Objects.nonNull(support.getLogWrapper())) {
                long costTime = SystemClock.now() - startTime;
                LogTracer logTracer = LogTracer.builder()
                        .taskName(this.getName())
                        .taskCost(costTime)
                        .executeState(exeError == null ? true : false)
                        .errorMessage(exeError == null ? Strings.EMPTY : exeError.getMessage())
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
        if (!exceptionInterceptor(exception)) {
            return;
        }
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
    public <Result> Result getResult(TaskSupport support, Class<? extends ITask> clazz, Class<Result> type) {
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
        return resultMap.get(this.getClass()) != null ? resultMap.get(this.getClass()) : resultMap.get(depKey(this.getClass()));
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
    public <Result> TaskResult<Result> getTaskResult(TaskSupport support, Class<? extends ITask> clazz, Class<Result> type) {
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
        if (Objects.nonNull(taskResult)) {
            return (Param) taskResult;
        }
        return null;
    }


    /**
     * Gets task future.
     *
     * @param <Result> the type parameter
     * @param support  the support
     * @param clazz    the clazz
     * @param type     the type
     * @return the task future
     */
    public <Result> Future<Result> getTaskFuture(TaskSupport support, Class<? extends ITask> clazz, Class<Result> type) {
        Object o = support.getTaskLoader().futureMaps.get(clazz);
        if (Objects.nonNull(o)) {
            return ((Future<Result>) o);
        }
        return null;
    }


    /**
     * Gets task future result.
     *
     * @param <Result> the type parameter
     * @param support  the support
     * @param clazz    the clazz
     * @param type     the type
     * @param timeout  the timeout
     * @param unit     the unit
     * @return the task future result
     */
    public <Result> Object getTaskFutureResult(TaskSupport support, Class<? extends ITask> clazz, Class<Result> type, long timeout, TimeUnit unit) {
        Object o = support.getTaskLoader().futureMaps.get(clazz);
        if (Objects.nonNull(o)) {
            try {
                return ((Future<Result>) o).get(timeout, unit);
            } catch (Exception e) {
                logger.error("task {} getTaskFuture error {}", this.getName(), e);
            }
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
            ErrorCallback<Param> errorCallback = new ErrorCallback<Param>(() -> support.getParam(), null, support, this);
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

            ErrorCallback<Param> errorCallback = new ErrorCallback<Param>(() -> support.getParam(), null, support, this);
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Gets timeout in milliseconds.
     *
     * @return the timeout in milliseconds
     */
    public int getTimeoutInMilliseconds() {
        return timeoutInMilliseconds;
    }

    /**
     * Sets timeout in milliseconds.
     *
     * @param timeoutInMilliseconds the timeout in milliseconds
     */
    public void setTimeoutInMilliseconds(int timeoutInMilliseconds) {
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }
}
