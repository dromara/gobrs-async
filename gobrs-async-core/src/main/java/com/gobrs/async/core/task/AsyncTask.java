package com.gobrs.async.core.task;


import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.common.enums.TaskEnum;
import com.gobrs.async.core.common.exception.AsyncTaskTimeoutException;
import com.gobrs.async.core.common.util.SystemClock;
import com.gobrs.async.core.config.ConfigManager;
import com.gobrs.async.core.log.LogTracer;
import com.gobrs.async.core.log.LogWrapper;
import com.gobrs.async.core.log.TraceUtil;
import com.gobrs.async.core.common.def.DefaultConfig;
import com.gobrs.async.core.common.domain.AnyConditionResult;
import com.gobrs.async.core.common.domain.TaskResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.Objects;

import static com.gobrs.async.core.common.def.DefaultConfig.TASK_TIMEOUT;
import static com.gobrs.async.core.common.def.FixSave.LOGGER_PLUGIN;
import static com.gobrs.async.core.common.enums.InterruptEnum.INIT;
import static com.gobrs.async.core.common.enums.InterruptEnum.INTERRUPTTING;
import static com.gobrs.async.core.common.util.ExceptionUtil.excludeInterceptException;

/**
 * The type Async com.gobrs.async.com.gobrs.async.test.task.
 * 每一个任务需要继承此 抽象类
 *
 * @param <Param>  the type parameter
 * @param <Result> the type parameter
 * @program: gobrs -async-starter
 * @ClassName AsyncTask<?, ?>
 * @description:
 * @author: sizegang
 * @create: 2022 -03-16
 */
@Data
@Slf4j
public abstract class AsyncTask<Param, Result> implements GobrsTask<Param> {

    /**da
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
    private boolean callback = DefaultConfig.TRANSACTION;

    private int retryCount = DefaultConfig.RETRY_COUNT;

    /**
     * Whether to execute a subtask if it fails
     */
    private boolean failSubExec = DefaultConfig.FAIL_SUB_EXEC;

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
     * 任务类型 class method 默认 class
     */
    private Integer type = TaskEnum.CLASS.getType();

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
            return (Result) AnyConditionResult.builder().result(taskResult.getResult()).build();
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
     * @throws Exception the exception
     */
    public Result taskAdapter(Param param, TaskSupport support) throws Exception {
        Long startTime = SystemClock.now();
        Result task;
        Exception exeError = null;
        try {
            task = task(param, support);
        } catch (Exception exception) {
            exeError = exception;
            exeError = transferException(exeError, support.getStatus(getName()).getStatus().get());
            throw exeError;
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
                if (!LOGGER_PLUGIN) {
                    log.info("<{}> [{}]", logWrapper.getTraceId(), this.getName());
                } else {
                    log.info("[{}]", this.getName());
                }

            }
        }
        return task;
    }

    /**
     * 异常转换
     *
     * @param e     the e
     * @param state the state
     * @return the exception
     */
    protected Exception transferException(Exception e, Integer state) {
        if (TASK_TIMEOUT == state) {
            return new AsyncTaskTimeoutException(String.format("task %s timeout exception", this.getName()), e);
        }
        return e;
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
        if (!excludeInterceptException(exception)) {
            return;
        }
        boolean logable = ConfigManager.Action.errLogabled(support.getRuleName());
        if (logable) {
            log.error("<{}> {} error", TraceUtil.get(), this.getName(), exception);
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
        Map<String, TaskResult> resultMap = support.getResultMap();
        return resultMap.get(getName());
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
        Map<String, TaskResult> resultMap = support.getResultMap();
        return resultMap.get(getName()) != null ? (TaskResult<Result>) resultMap.get(getName()) : null;
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
            // 设置标识
            return support.getTaskLoader().getINTERRUPTFLAG().compareAndSet(INIT.getState(), INTERRUPTTING.getState());
        } catch (Exception ex) {
            log.error("stopAsync error {}", ex);
            return false;
        }
    }

    /**
     * Stop async boolean.
     *
     * @param support the support
     * @param cusCode 开发者自定义状态码
     * @return the boolean
     */
    public boolean stopAsync(TaskSupport support, Integer cusCode) {
        try {
            boolean b = support.getTaskLoader().getINTERRUPTFLAG().compareAndSet(INIT.getState(), INTERRUPTTING.getState());
            if (b) {
                support.getTaskLoader().setCusCode(cusCode);
            }
            return b;
        } catch (Exception ex) {
            log.error("stopAsync error {} ", ex);
            return false;
        }
    }


    /**
     * Gets process trace id.
     *
     * @return the trace id
     */
    public String getProcessTraceId() {
        Object tr = TraceUtil.get();
        return tr != null ? tr.toString() : null;
    }

    /**
     * Gets formatted trace id.
     *
     * @return the formatted trace id
     */
    public String getFormattedTraceId() {
        String processTraceId = getProcessTraceId();
        return String.format("<%s> [%s]", processTraceId, this.getName());
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
