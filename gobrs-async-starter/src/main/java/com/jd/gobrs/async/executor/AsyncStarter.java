package com.jd.gobrs.async.executor;


import com.jd.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.jd.gobrs.async.callback.DefaultGroupCallback;
import com.jd.gobrs.async.callback.IGroupCallback;
import com.jd.gobrs.async.constant.StateConstant;
import com.jd.gobrs.async.exception.AsyncExceptionInterceptor;
import com.jd.gobrs.async.gobrs.GobrsAsyncSupport;
import com.jd.gobrs.async.gobrs.GobrsFlowState;
import com.jd.gobrs.async.result.AsyncResult;
import com.jd.gobrs.async.spring.GobrsSpring;
import com.jd.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;
import com.jd.gobrs.async.wrapper.TaskWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.jd.gobrs.async.constant.StateConstant.INIT;
import static com.jd.gobrs.async.constant.StateConstant.WORKING;


/**
 * 类入口，可以根据自己情况调整core线程的数量
 *
 * @author sizegang wrote on 2019-12-18
 * @version 1.0
 */
public class AsyncStarter {

    private static Logger logger = LoggerFactory.getLogger(TaskWrapper.class);

    private static AsyncExceptionInterceptor asyncExceptionInterceptor = GobrsSpring.getBean(AsyncExceptionInterceptor.class);


    private static GobrsAsyncProperties gobrsAsyncProperties = GobrsSpring.getBean(GobrsAsyncProperties.class);
    /**
     * 默认不定长线程池
     */
    private static final ThreadPoolExecutor COMMON_POOL = GobrsSpring.getBean(GobrsAsyncThreadPoolFactory.class).getThreadPoolExecutor();

    /**
     * 注意，这里是个static，也就是只能有一个线程池。用户自定义线程池时，也只能定义一个
     */
    private static ThreadPoolExecutor executorService;

    /**
     * 出发点
     */
    public static AsyncResult startTaskFlow(long timeout, ThreadPoolExecutor executorService, List<TaskWrapper> taskWrappers, Map<String, Object> params) {

        if (taskWrappers == null || taskWrappers.size() == 0) {
            return null;
        }
        GobrsAsyncSupport.SupportBuilder builder = GobrsAsyncSupport.builder();
        GobrsAsyncSupport support = builder.params(params).build();
        //Save thread pool variables
        AsyncStarter.executorService = executorService;
        CompletableFuture[] futures = new CompletableFuture[taskWrappers.size()];
        // Start task Flow
        starting(support);
        for (int i = 0; i < taskWrappers.size(); i++) {
            TaskWrapper wrapper = taskWrappers.get(i);
            futures[i] = CompletableFuture.runAsync(() -> wrapper.task(executorService, wrapper, timeout, support), executorService)
                    .exceptionally((ex) -> {
                        boolean state = gobrsAsyncProperties.isTaskInterrupt() &&
                                GobrsFlowState.compareAndSetState(support.getTaskFlowState(), WORKING, StateConstant.ERROR);
                        throw asyncExceptionInterceptor.exception(ex, state);
                    });
        }
        try {
            CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
            return buildResult(support);
        } catch (Exception e) {
            Set<TaskWrapper> set = new HashSet<>();
            totalWorkers(taskWrappers, set);
            for (TaskWrapper wrapper : set) {
                wrapper.stopNow(support);
            }
            return buildResult(support, e);
        }
    }


    private static boolean starting(GobrsAsyncSupport support) {
        return support.getTaskFlowState().compareAndSet(INIT, WORKING);
    }

    private static AsyncResult buildResult(GobrsAsyncSupport support) {
        AsyncResult asyncResult = new AsyncResult();
        asyncResult.setExpCode(support.getExpCode());
        asyncResult.setResultMap(support.getWorkResult());
        asyncResult.setSuccess(true);
        return asyncResult;
    }

    private static AsyncResult buildResult(GobrsAsyncSupport support, Exception exception) {
        AsyncResult asyncResult = new AsyncResult();
        asyncResult.setExpCode(support.getExpCode());
        asyncResult.setResultMap(support.getWorkResult());
        asyncResult.setException(exception);
        asyncResult.setSuccess(false);
        return asyncResult;
    }

    /**
     * 如果想自定义线程池，请传pool。不自定义的话，就走默认的COMMON_POOL
     */
    public static AsyncResult startTaskFlow(long timeout, ThreadPoolExecutor executorService, Map<String, Object> parameters, TaskWrapper... workerWrapper) {
        if (workerWrapper == null || workerWrapper.length == 0) {
            return null;
        }
        List<TaskWrapper> taskWrappers = Arrays.stream(workerWrapper).collect(Collectors.toList());
        return startTaskFlow(timeout, executorService, taskWrappers, parameters);
    }

    /**
     * 同步阻塞,直到所有都完成,或失败
     */
    public static AsyncResult startTaskFlow(long timeout, Map<String, Object> parameters, TaskWrapper... workerWrapper) {
        return startTaskFlow(timeout, COMMON_POOL, parameters, workerWrapper);
    }

    public static AsyncResult startTaskFlow(long timeout, List<TaskWrapper> workerWrapper, Map<String, Object> params) {
        return startTaskFlow(timeout, COMMON_POOL, workerWrapper, params);
    }


    public static void startTaskFlowAsync(long timeout, IGroupCallback groupCallback, Map<String, Object> params, TaskWrapper... workerWrapper) {
        startTaskFlowAsync(timeout, COMMON_POOL, groupCallback, params, workerWrapper);
    }

    /**
     * 异步执行,直到所有都完成,或失败后，发起回调
     */
    public static void startTaskFlowAsync(long timeout, ThreadPoolExecutor executorService, IGroupCallback groupCallback, Map<String, Object> params, TaskWrapper... workerWrapper) {
        if (groupCallback == null) {
            groupCallback = new DefaultGroupCallback();
        }
        IGroupCallback finalGroupCallback = groupCallback;
        if (executorService != null) {
            executorService.submit(() -> {
                try {
                    AsyncResult asyncResult = startTaskFlow(timeout, executorService, params, workerWrapper);
                    if (Objects.nonNull(asyncResult)) {
                        finalGroupCallback.success(Arrays.asList(workerWrapper));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
                }
            });
        } else {
            COMMON_POOL.submit(() -> {
                try {
                    AsyncResult asyncResult = startTaskFlow(timeout, COMMON_POOL, params, workerWrapper);
                    if (Objects.nonNull(asyncResult)) {
                        finalGroupCallback.success(Arrays.asList(workerWrapper));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
                }
            });
        }

    }

    /**
     * 总共多少个执行单元
     */
    @SuppressWarnings("unchecked")
    private static void totalWorkers(List<TaskWrapper> taskWrappers, Set<TaskWrapper> set) {
        set.addAll(taskWrappers);
        for (TaskWrapper wrapper : taskWrappers) {
            if (wrapper.getNextWrappers() == null) {
                continue;
            }
            List<TaskWrapper> wrappers = wrapper.getNextWrappers();
            totalWorkers(wrappers, set);
        }

    }

    /**
     * 关闭线程池
     */
    public static void shutDown() {
        shutDown(executorService);
    }

    /**
     * 关闭线程池
     */
    public static void shutDown(ThreadPoolExecutor executorService) {
        if (executorService != null) {
            executorService.shutdown();
        } else {
            COMMON_POOL.shutdown();
        }
    }
}
