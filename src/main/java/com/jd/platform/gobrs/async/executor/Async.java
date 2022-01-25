package com.jd.platform.gobrs.async.executor;


import com.jd.platform.gobrs.async.callback.DefaultGroupCallback;
import com.jd.platform.gobrs.async.callback.IGroupCallback;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 类入口，可以根据自己情况调整core线程的数量
 * @author sizegang wrote on 2019-12-18
 * @version 1.0
 */
public class Async {
    /**
     * 默认不定长线程池
     */
    private static final ThreadPoolExecutor COMMON_POOL = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    /**
     * 注意，这里是个static，也就是只能有一个线程池。用户自定义线程池时，也只能定义一个
     */
    private static ExecutorService executorService;

    /**
     * 出发点
     */
    public static boolean beginTaskFlow(long timeout, ExecutorService executorService, List<TaskWrapper> taskWrappers) throws ExecutionException, InterruptedException {
        if(taskWrappers == null || taskWrappers.size() == 0) {
            return false;
        }
        //保存线程池变量
        Async.executorService = executorService;
        //定义一个map，存放所有的wrapper，key为wrapper的唯一id，value是该wrapper，可以从value中获取wrapper的result
        Map<String, TaskWrapper> forParamUseWrappers = new ConcurrentHashMap<>();
        CompletableFuture[] futures = new CompletableFuture[taskWrappers.size()];
        for (int i = 0; i < taskWrappers.size(); i++) {
            TaskWrapper wrapper = taskWrappers.get(i);
            futures[i] = CompletableFuture.runAsync(() -> wrapper.task(executorService, timeout, forParamUseWrappers), executorService);
        }
        try {
            CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
            return true;
        } catch (TimeoutException e) {
            Set<TaskWrapper> set = new HashSet<>();
            totalWorkers(taskWrappers, set);
            for (TaskWrapper wrapper : set) {
                wrapper.stopNow();
            }
            return false;
        }
    }

    /**
     * 如果想自定义线程池，请传pool。不自定义的话，就走默认的COMMON_POOL
     */
    public static boolean beginTaskFlow(long timeout, ExecutorService executorService, TaskWrapper... workerWrapper) throws ExecutionException, InterruptedException {
        if(workerWrapper == null || workerWrapper.length == 0) {
            return false;
        }
        List<TaskWrapper> taskWrappers =  Arrays.stream(workerWrapper).collect(Collectors.toList());
        return beginTaskFlow(timeout, executorService, taskWrappers);
    }

    /**
     * 同步阻塞,直到所有都完成,或失败
     */
    public static boolean beginTaskFlow(long timeout, TaskWrapper... workerWrapper) throws ExecutionException, InterruptedException {
        return beginTaskFlow(timeout, COMMON_POOL, workerWrapper);
    }

    public static void beginTaskFlowAsync(long timeout, IGroupCallback groupCallback, TaskWrapper... workerWrapper) {
        beginTaskFlowAsync(timeout, COMMON_POOL, groupCallback, workerWrapper);
    }

    /**
     * 异步执行,直到所有都完成,或失败后，发起回调
     */
    public static void beginTaskFlowAsync(long timeout, ExecutorService executorService, IGroupCallback groupCallback, TaskWrapper... workerWrapper) {
        if (groupCallback == null) {
            groupCallback = new DefaultGroupCallback();
        }
        IGroupCallback finalGroupCallback = groupCallback;
        if (executorService != null) {
            executorService.submit(() -> {
                try {
                    boolean success = beginTaskFlow(timeout, executorService, workerWrapper);
                    if (success) {
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
                    boolean success = beginTaskFlow(timeout, COMMON_POOL, workerWrapper);
                    if (success) {
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
    public static void shutDown(ExecutorService executorService) {
        if (executorService != null) {
            executorService.shutdown();
        } else {
            COMMON_POOL.shutdown();
        }
    }

    public static String getThreadCount() {
        return "activeCount=" + COMMON_POOL.getActiveCount() +
                "  completedCount " + COMMON_POOL.getCompletedTaskCount() +
                "  largestCount " + COMMON_POOL.getLargestPoolSize();
    }
}
