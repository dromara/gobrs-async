package com.gobrs.async.core.threadpool;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.gobrs.async.core.config.GobrsAsyncRule;
import com.gobrs.async.core.config.GobrsConfig;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * The type Gobrs async thread pool factory.
 *
 * @program: gobrs -async-starter
 * @ClassName GobrsAsyncThreadPoolFactory
 * @description: Thread pool factory
 * @author: sizegang
 * @create: 2022 -02-20
 * @Version 1.0
 */
public class GobrsAsyncThreadPoolFactory {

    private GobrsConfig gobrsConfig;


    /**
     * The constant cachedExecutors.
     */
    public static final Map<String, ExecutorService> cachedExecutors = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Gobrs async thread pool factory.
     *
     * @param gobrsConfig the gobrs config
     */
    public GobrsAsyncThreadPoolFactory(GobrsConfig gobrsConfig) {
        this.gobrsConfig = gobrsConfig;
        this.COMMON_POOL = defaultThreadPool(gobrsConfig.getThreadPool());
        rulelThreadPool();
    }

    private ExecutorService defaultThreadPool(GobrsConfig.ThreadPool threadPool) {
        return TtlExecutors.getTtlExecutorService(createThreadPool(threadPool));
    }

    /**
     * 创建规则的线程池 隔离
     */
    private void rulelThreadPool() {
        List<GobrsAsyncRule> rules = gobrsConfig.getRules();
        for (GobrsAsyncRule rule : rules) {
            cachedExecutors.put(rule.getName(), defaultThreadPool(rule.getThreadPool()));
        }
    }

    /**
     * The default thread pool is not long
     */
    private ExecutorService COMMON_POOL;


    /**
     * Gets thread pool executor.
     *
     * @return the thread pool executor
     */
    public ExecutorService getDefaultThreadPool() {
        return COMMON_POOL;
    }

    /**
     * Gets thread pool.
     *
     * @param ruleName the rule name
     * @return the thread pool
     */
    public ExecutorService getThreadPool(String ruleName) {
        return cachedExecutors.get(ruleName);
    }

    /**
     * The user dynamically sets the thread pool parameters
     *
     * @param threadPoolExecutor the thread pool executor
     */
    public void setDefaultThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        this.COMMON_POOL = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
        rulelThreadPool();
    }

    /**
     * Sets thread pool executor.
     *
     * @param ruleName           the rule name
     * @param threadPoolExecutor the thread pool executor
     */
    public void setThreadPoolExecutor(String ruleName, ExecutorService threadPoolExecutor) {
        cachedExecutors.put(ruleName, TtlExecutors.getTtlExecutorService(threadPoolExecutor));
    }


    private ExecutorService defaultThreadPool() {
        return COMMON_POOL;
    }

    /**
     * Create default thread pool thread pool executor.
     * 创建默认线程池
     *
     * @param threadPool the thread pool
     * @return the thread pool executor
     */
    ExecutorService createThreadPool(GobrsConfig.ThreadPool threadPool) {
        if (Objects.isNull(threadPool)) {
            if (COMMON_POOL != null) {
                return COMMON_POOL;
            }
            return Executors.newCachedThreadPool();
        }
        return new ThreadPoolExecutor(threadPool.getCorePoolSize(),
                threadPool.getMaxPoolSize(), threadPool.getKeepAliveTime(), threadPool.getTimeUnit(),
                threadPool.getWorkQueue(), ThreadPoolBuilder.caseReject(threadPool.getRejectedExecutionHandler()));
    }


}
