package com.jd.gobrs.async.executor;


import com.jd.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.jd.gobrs.async.callback.DefaultGroupCallback;
import com.jd.gobrs.async.callback.IGroupCallback;
import com.jd.gobrs.async.gobrs.GobrsFlowState;
import com.jd.gobrs.async.result.AsyncResult;
import com.jd.gobrs.async.spring.GobrsSpring;
import com.jd.gobrs.async.threadpool.GobrsAsyncThreadPoolFactory;
import com.jd.gobrs.async.util.SnowflakeId;
import com.jd.gobrs.async.wrapper.TaskWrapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 类入口，可以根据自己情况调整core线程的数量
 *
 * @author sizegang wrote on 2019-12-18
 * @version 1.0
 */
public class Async {
    /**
     * 默认不定长线程池
     */
    private static final ThreadPoolTaskExecutor COMMON_POOL = GobrsSpring.getBean(GobrsAsyncThreadPoolFactory.class).getThreadPoolExecutor();

    /**
     * 获取释放资源线程池
     */
    private static final ThreadPoolTaskExecutor RELEASE_THREADPOOL = GobrsSpring.getBean(GobrsAsyncThreadPoolFactory.class).getReleaseThreadPoolExecutor();
    /**
     * 注意，这里是个static，也就是只能有一个线程池。用户自定义线程池时，也只能定义一个
     */
    private static ThreadPoolTaskExecutor executorService;

    private static SnowflakeId snowflakeId = new SnowflakeId(0, 0);

    /**
     * 出发点
     */
    public static AsyncResult startTaskFlow(long timeout, ThreadPoolTaskExecutor executorService, List<TaskWrapper> taskWrappers, Map<String, Object> params) throws ExecutionException, InterruptedException {

        long businessId = snowflakeId.nextId();
        if (taskWrappers == null || taskWrappers.size() == 0) {
            return null;
        }
        //保存线程池变量
        Async.executorService = executorService;
        //定义一个map，存放所有的wrapper，key为wrapper的唯一id，value是该wrapper，可以从value中获取wrapper的result
        Map<String, TaskWrapper> dataSource = new ConcurrentHashMap<>();
        CompletableFuture[] futures = new CompletableFuture[taskWrappers.size()];
        for (int i = 0; i < taskWrappers.size(); i++) {
            TaskWrapper wrapper = taskWrappers.get(i);
            futures[i] = CompletableFuture.runAsync(() -> wrapper.task(executorService, timeout, dataSource, params, businessId), executorService);
        }
        try {
            CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
            return buildResult(dataSource, businessId);
        } catch (Exception e) {
            Set<TaskWrapper> set = new HashSet<>();
            totalWorkers(taskWrappers, set);
            for (TaskWrapper wrapper : set) {
                wrapper.stopNow(businessId);
            }
            return buildResult(dataSource, businessId);
        } finally {
            // 释放资源
            RELEASE_THREADPOOL.execute(() -> {
                release(taskWrappers, businessId);
            });
        }
    }

    private static AsyncResult buildResult(Map<String, TaskWrapper> dataSource, Long businessId) {
        AsyncResult asyncResult = new AsyncResult();
        asyncResult.setDatasources(dataSource);
        asyncResult.setBusinessId(businessId);
        GobrsFlowState.GobrsState gobrsState = GobrsFlowState.gobrsFlowState.get(businessId);
        if (gobrsState != null) {
            asyncResult.setExpCode(gobrsState.getExpCode());
        }
        return asyncResult;
    }

    private static void release(List<TaskWrapper> taskWrappers, Long businessId) {
        taskWrappers.parallelStream().forEach(x -> {
            doRelease(x.getNextWrappers(), businessId);
        });
    }

    private static void doRelease(List<TaskWrapper> taskWrappers, Long businessId) {
        if (taskWrappers == null) {
            return;
        }
        taskWrappers.parallelStream().forEach(x -> {
            x.workResult.remove(businessId);
            x.state.remove(businessId);
            GobrsFlowState.gobrsFlowState.remove(businessId);
            doRelease(x.getNextWrappers(), businessId);
        });
    }

    /**
     * 如果想自定义线程池，请传pool。不自定义的话，就走默认的COMMON_POOL
     */
    public static AsyncResult startTaskFlow(long timeout, ThreadPoolTaskExecutor executorService, Map<String, Object> parameters, TaskWrapper... workerWrapper) throws ExecutionException, InterruptedException {
        if (workerWrapper == null || workerWrapper.length == 0) {
            return null;
        }
        List<TaskWrapper> taskWrappers = Arrays.stream(workerWrapper).collect(Collectors.toList());
        return startTaskFlow(timeout, executorService, taskWrappers, parameters);
    }

    /**
     * 同步阻塞,直到所有都完成,或失败
     */
    public static AsyncResult startTaskFlow(long timeout, Map<String, Object> parameters, TaskWrapper... workerWrapper) throws ExecutionException, InterruptedException {
        return startTaskFlow(timeout, COMMON_POOL, parameters, workerWrapper);
    }

    public static AsyncResult startTaskFlow(long timeout, List<TaskWrapper> workerWrapper, Map<String, Object> params) throws ExecutionException, InterruptedException {
        return startTaskFlow(timeout, COMMON_POOL, workerWrapper, params);
    }


    public static void startTaskFlowAsync(long timeout, IGroupCallback groupCallback, Map<String, Object> params, TaskWrapper... workerWrapper) {
        startTaskFlowAsync(timeout, COMMON_POOL, groupCallback, params, workerWrapper);
    }

    /**
     * 异步执行,直到所有都完成,或失败后，发起回调
     */
    public static void startTaskFlowAsync(long timeout, ThreadPoolTaskExecutor executorService, IGroupCallback groupCallback, Map<String, Object> params, TaskWrapper... workerWrapper) {
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
                } catch (ExecutionException | InterruptedException e) {
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
                } catch (ExecutionException | InterruptedException e) {
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
    public static void shutDown(ThreadPoolTaskExecutor executorService) {
        if (executorService != null) {
            executorService.shutdown();
        } else {
            COMMON_POOL.shutdown();
        }
    }

    public static String getThreadCount() {
        return "getPoolSize=" + COMMON_POOL.getPoolSize() +
                "  getCorePoolSize " + COMMON_POOL.getCorePoolSize() +
                "  getMaxPoolSize " + COMMON_POOL.getMaxPoolSize();
    }
}
