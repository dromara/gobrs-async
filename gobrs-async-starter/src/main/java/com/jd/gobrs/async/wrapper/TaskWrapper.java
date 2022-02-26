package com.jd.gobrs.async.wrapper;

import com.jd.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.jd.gobrs.async.callback.DefaultCallback;
import com.jd.gobrs.async.callback.ICallback;
import com.jd.gobrs.async.constant.GobrsAsyncConstant;
import com.jd.gobrs.async.constant.StateConstant;
import com.jd.gobrs.async.exception.AsyncExceptionInterceptor;
import com.jd.gobrs.async.exception.SkippedException;
import com.jd.gobrs.async.gobrs.GobrsFlowState;
import com.jd.gobrs.async.spring.GobrsSpring;
import com.jd.gobrs.async.task.DependWrapper;
import com.jd.gobrs.async.task.ResultState;
import com.jd.gobrs.async.callback.ITask;
import com.jd.gobrs.async.executor.timer.SystemClock;
import com.jd.gobrs.async.task.TaskResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.*;
import java.util.concurrent.*;

/**
 * 对每个worker及callback进行包装，一对一
 *
 * @author sizegang wrote on 2019-11-19.
 */
public class TaskWrapper<T, V> {
    /**
     * 该wrapper的唯一标识
     */
    private String id;
    /**
     * worker将来要处理的param
     */
    private T param;
    private ITask<T, V> worker;
    private ICallback<T, V> callback;
    /**
     * 在自己后面的wrapper，如果没有，自己就是末尾；如果有一个，就是串行；如果有多个，有几个就需要开几个线程</p>
     * -------2
     * 1
     * -------3
     * 如1后面有2、3
     */
    private List<TaskWrapper<?, ?>> nextWrappers;
    /**
     * 依赖的wrappers，有2种情况，1:必须依赖的全部完成后，才能执行自己 2:依赖的任何一个、多个完成了，就可以执行自己
     * 通过must字段来控制是否依赖项必须完成
     * 1
     * -------3
     * 2
     * 1、2执行完毕后才能执行3
     */
    private List<DependWrapper> dependWrappers;

    private AsyncExceptionInterceptor asyncExceptionInterceptor = GobrsSpring.getBean(AsyncExceptionInterceptor.class);


    private GobrsAsyncProperties gobrsAsyncProperties = GobrsSpring.getBean(GobrsAsyncProperties.class);

    public List<DependWrapper> getDependWrappers() {
        return dependWrappers;
    }


    public void setDependWrappers(List<DependWrapper> dependWrappers) {
        this.dependWrappers = dependWrappers;
    }

    /**
     * 标记该事件是否已经被处理过了，譬如已经超时返回false了，后续rpc又收到返回值了，则不再二次回调
     * 经试验,volatile并不能保证"同一毫秒"内,多线程对该值的修改和拉取
     * <p>
     * 1-finish, 2-error, 3-working
     */
    public volatile ConcurrentHashMap<Long, Integer> state = new ConcurrentHashMap<>();
    /**
     * 该map存放所有wrapper的id和wrapper映射
     */
    private Map<String, TaskWrapper> forParamUseWrappers;
    /**
     * 也是个钩子变量，用来存临时的结果
     */
    public volatile ConcurrentHashMap<Long, TaskResult<V>> workResult = new ConcurrentHashMap<>();
//    private volatile TaskResult<V> workResult = TaskResult.defaultResult();
    /**
     * 是否在执行自己前，去校验nextWrapper的执行结果<p>
     * 1   4
     * -------3
     * 2
     * 如这种在4执行前，可能3已经执行完毕了（被2执行完后触发的），那么4就没必要执行了。
     * 注意，该属性仅在nextWrapper数量<=1时有效，>1时的情况是不存在的
     */
    private volatile boolean needCheckNextWrapperResult = true;

    private static final int FINISH = StateConstant.FINISH;
    private static final int ERROR = StateConstant.ERROR;
    private static final int WORKING = StateConstant.WORKING;
    private static final int INIT = StateConstant.INIT;

    private TaskWrapper(String id, ITask<T, V> worker, T param, ICallback<T, V> callback) {
        if (worker == null) {
            throw new NullPointerException("async.worker is null");
        }
        this.worker = worker;
        this.param = param;
        this.id = id;
        //允许不设置回调
        if (callback == null) {
            callback = new DefaultCallback<>();
        }
        this.callback = callback;
    }

    /**
     * 开始工作
     * fromWrapper 代表这次work是由哪个上游wrapper发起的
     */
    private void task(ThreadPoolTaskExecutor executorService, TaskWrapper fromWrapper, long remainTime, Map<String, TaskWrapper> forParamUseWrappers,
                      Map<String, Object> params, Long businessId) {
        this.forParamUseWrappers = forParamUseWrappers;
        //将自己放到所有wrapper的集合里去
        forParamUseWrappers.put(id, this);
        long now = SystemClock.now();
        //总的已经超时了，就快速失败，进行下一个
        if (remainTime <= 0) {
            taskFail(INIT, null, businessId);
            nextTask(executorService, now, remainTime, params, businessId);
            return;
        }
        //如果自己已经执行过了。
        //可能有多个依赖，其中的一个依赖已经执行完了，并且自己也已开始执行或执行完毕。当另一个依赖执行完毕，又进来该方法时，就不重复处理了
        if (getState(businessId) == FINISH || getState(businessId) == ERROR) {
            nextTask(executorService, now, remainTime, params, businessId);
            return;
        }

        //如果在执行前需要校验nextWrapper的状态  默认true
        if (needCheckNextWrapperResult) {
            //如果自己的next链上有已经出结果或已经开始执行的任务了，自己就不用继续了 ，有结果返回fales 直接进行下一个task  并return
            if (!checkNextWrapperResult(businessId)) {
                taskFail(INIT, new SkippedException(), businessId);
                nextTask(executorService, now, remainTime, params, businessId);
                return;
            }
        }

        //如果没有任何依赖，说明自己就是第一批要执行的
        if (dependWrappers == null || dependWrappers.size() == 0) {
            doTask(params, businessId);
            nextTask(executorService, now, remainTime, params, businessId);
            return;
        }

        /*如果有前方依赖，存在两种情况
         一种是前面只有一个wrapper。即 A  ->  B
        一种是前面有多个wrapper。A C D ->   B。需要A、C、D都完成了才能轮到B。但是无论是A执行完，还是C执行完，都会去唤醒B。
        所以需要B来做判断，必须A、C、D都完成，自己才能执行 */

        //只有一个依赖
        if (dependWrappers.size() == 1) {
            doDependsOneJob(fromWrapper, params, businessId);
            nextTask(executorService, now, remainTime, params, businessId);
        } else {
            //有多个依赖时
            doDependsJobs(executorService, dependWrappers, fromWrapper, now, remainTime, params, businessId);
        }

    }


    public void task(ThreadPoolTaskExecutor executorService, long remainTime, Map<String, TaskWrapper> forParamUseWrappers, Map<String, Object> params, Long businessId) {
        task(executorService, null, remainTime, forParamUseWrappers, params, businessId);
    }

    /**
     * 总控制台超时，停止所有任务
     */
    public void stopNow(Long businessId) {
        if (getState(businessId) == INIT || getState(businessId) == WORKING) {
            taskFail(getState(businessId), null, businessId);
        }
    }

    /**
     * 判断自己下游链路上，是否存在已经出结果的或已经开始执行的
     * 如果没有返回true，如果有返回false
     */
    private boolean checkNextWrapperResult(Long businessId) {
        //如果自己就是最后一个，或者后面有并行的多个，就返回true
        if (nextWrappers == null || nextWrappers.size() != 1) {
            return getState(businessId) == INIT;
        }
        TaskWrapper nextWrapper = nextWrappers.get(0);
        boolean state = nextWrapper.getState(businessId) == INIT;
        //继续校验自己的next的状态
        return state && nextWrapper.checkNextWrapperResult(businessId);
    }

    /**
     * 进行下一个任务
     */
    private void nextTask(ThreadPoolTaskExecutor executorService, long now, long remainTime, Map<String, Object> params, Long businessId) {
        //花费的时间
        long costTime = SystemClock.now() - now;
        if (nextWrappers == null) {
            return;
        }
        if (nextWrappers.size() == 1) {
            nextWrappers.get(0).task(executorService, TaskWrapper.this, remainTime - costTime, forParamUseWrappers, params, businessId);
            return;
        }
        CompletableFuture[] futures = new CompletableFuture[nextWrappers.size()];
        for (int i = 0; i < nextWrappers.size(); i++) {
            int finalI = i;
            futures[i] = CompletableFuture.runAsync(() -> nextWrappers.get(finalI)
                            .task(executorService, TaskWrapper.this,
                                    remainTime - costTime, forParamUseWrappers, params, businessId), executorService)
                    .exceptionally((ex) -> {
                        boolean state = gobrsAsyncProperties.isTaskInterrupt() &&
                                GobrsFlowState.compareAndSetState(StateConstant.WORKING, StateConstant.ERROR, businessId);
                        throw asyncExceptionInterceptor.exception(ex, state);
                    });
        }
        CompletableFuture.allOf(futures).join();
    }

    private void doDependsOneJob(TaskWrapper dependWrapper, Map<String, Object> params, Long businessId) {
        if (ResultState.TIMEOUT == dependWrapper.workResult.get(businessId)) {
            workResult.put(businessId, defaultResult());
            taskFail(INIT, null, businessId);
        } else if (ResultState.EXCEPTION == dependWrapper.workResult.get(businessId)) {
            workResult.put(businessId, defaultExResult(dependWrapper.getWorkResult(businessId).getEx()));
            taskFail(INIT, null, businessId);
        } else {
            //前面任务正常完毕了，该自己了
            doTask(params, businessId);
        }
    }

    private void doDependsJobs(ThreadPoolTaskExecutor executorService, List<DependWrapper> dependWrappers, TaskWrapper fromWrapper,
                               long now, long remainTime, Map<String, Object> params, Long businessId) {
        boolean nowDependIsMust = false;
        //创建必须完成的上游wrapper集合
        Set<DependWrapper> mustWrapper = new HashSet<>();
        for (DependWrapper dependWrapper : dependWrappers) {
            if (dependWrapper.isMust()) {
                mustWrapper.add(dependWrapper);
            }
            if (dependWrapper.getDependWrapper().equals(fromWrapper)) {
                nowDependIsMust = dependWrapper.isMust();
            }
        }

        //如果全部是不必须的条件，那么只要到了这里，就执行自己。
        if (mustWrapper.size() == 0) {
            if (fromWrapper.getWorkResult(businessId) != null && ResultState.TIMEOUT == fromWrapper.getWorkResult(businessId).getResultState()) {
                taskFail(INIT, null, businessId);
            } else {
                doTask(params, businessId);
            }
            nextTask(executorService, now, remainTime, params, businessId);
            return;
        }

        //如果存在需要必须完成的，且fromWrapper不是必须的，就什么也不干
        if (!nowDependIsMust) {
            return;
        }

        //如果fromWrapper是必须的
        boolean existNoFinish = false;
        boolean hasError = false;
        //先判断前面必须要执行的依赖任务的执行结果，如果有任何一个失败，那就不用走doTask了，直接给自己设置为失败，进行下一步就是了
        for (DependWrapper dependWrapper : mustWrapper) {
            TaskWrapper workerWrapper = dependWrapper.getDependWrapper();
            TaskResult tempWorkResult = workerWrapper.getWorkResult(businessId);
            //为null或者isWorking，说明它依赖的某个任务还没执行到或没执行完
            if (workerWrapper.getState(businessId) == INIT || workerWrapper.getState(businessId) == WORKING) {
                existNoFinish = true;
                break;
            }
            if (tempWorkResult != null && ResultState.TIMEOUT == tempWorkResult.getResultState()) {
                workResult.put(businessId, tempWorkResult);
                hasError = true;
                break;
            }
            if (tempWorkResult != null && ResultState.EXCEPTION == tempWorkResult.getResultState()) {
                workResult.put(businessId, defaultExResult(workerWrapper.getWorkResult(businessId).getEx()));
                hasError = true;
                break;
            }

        }
        //只要有失败的
        if (hasError) {
            taskFail(INIT, null, businessId);
            nextTask(executorService, now, remainTime, params, businessId);
            return;
        }

        //如果上游都没有失败，分为两种情况，一种是都finish了，一种是有的在working
        //都finish的话
        if (!existNoFinish) {
            //上游都finish了，进行自己
            doTask(params, businessId);
            nextTask(executorService, now, remainTime, params, businessId);
            return;
        }
    }

    /**
     * 执行自己的job.具体的执行是在另一个线程里,但判断阻塞超时是在work线程
     */
    private void doTask(Map<String, Object> params, long businessId) {
        if (GobrsFlowState.assertState(StateConstant.STOP, businessId)) {
            return;
        }
        if (gobrsAsyncProperties.isTaskInterrupt() &&
                GobrsFlowState.assertState(StateConstant.STOP, businessId)) {
            return;
        }
        //执行结果
        doTaskDep(params, businessId);
    }

    /**
     * taskFail
     * 任务失败
     */
    private boolean taskFail(int expect, Exception e, Long businessId) {
        //试图将它从expect状态,改成Error CAS 无锁设置
        if (!compareAndSetState(expect, ERROR, businessId)) {
            return false;
        }

        //尚未处理过结果
        if (checkIsNullResult(businessId)) {
            if (e == null) {
                workResult.put(businessId, defaultResult());
            } else {
                workResult.put(businessId, defaultExResult(e));
            }
        }

        callback.result(false, param, workResult.get(businessId));
        return true;
    }

    /**
     * 真正的的单个task执行任务 doTask
     */
    private TaskResult<V> doTaskDep(Map<String, Object> params, Long businessId) {
        T param = params.containsKey(GobrsAsyncConstant.DEFAULT_PARAMS) == true
                ? (T) params.get(GobrsAsyncConstant.DEFAULT_PARAMS) : (T) params.get(id);
        TaskResult<V> objectTaskResult = TaskResult.defaultResult();
        //  先判断自己是否需要执行
        if (!worker.nessary(param)) {

            objectTaskResult.setResultState(ResultState.SUCCESS);
            objectTaskResult.setResult(null);
            workResult.put(businessId, objectTaskResult);
            return objectTaskResult;
        }
        //避免重复执行
        if (!checkIsNullResult(businessId)) {
            return objectTaskResult;
        }
        try {

            //如果已经不是init状态了，说明正在被执行或已执行完毕。这一步很重要，可以保证任务不被重复执行
            if (!compareAndSetState(INIT, WORKING, businessId)) {
                return objectTaskResult;
            }

            callback.begin();

            //执行耗时操作
            V resultValue = worker.task(param, forParamUseWrappers, businessId);

            //如果状态不是在working,说明别的地方已经修改了
            if (!compareAndSetState(WORKING, FINISH, businessId)) {
                return objectTaskResult;
            }

            objectTaskResult.setResultState(ResultState.SUCCESS);
            objectTaskResult.setResult(resultValue);
            workResult.put(businessId, objectTaskResult);
            //回调成功
            callback.result(true, param, objectTaskResult);

            return objectTaskResult;
        } catch (Exception e) {
            //避免重复回调
            if (!checkIsNullResult(businessId)) {
                return objectTaskResult;
            }
            taskFail(WORKING, e, businessId);
            if (gobrsAsyncProperties.isTaskInterrupt()) {
                throw e;
            }
            return objectTaskResult;
        }
    }

    public TaskResult<V> getWorkResult(Long businessId) {
        return workResult.get(businessId);
    }

    public List<TaskWrapper<?, ?>> getNextWrappers() {
        return nextWrappers;
    }

    public void setParam(T param) {
        this.param = param;
    }


    private boolean checkIsNullResult(Long businessId) {
        return workResult.get(businessId) == null;
    }

    public void addDepend(TaskWrapper<?, ?> workerWrapper, boolean must) {
        addDepend(new DependWrapper(workerWrapper, must));
    }

    public void addDepend(DependWrapper dependWrapper) {
        if (dependWrappers == null) {
            dependWrappers = new ArrayList<>();
        }
        //如果依赖的是重复的同一个，就不重复添加了
        for (DependWrapper wrapper : dependWrappers) {
            if (wrapper.equals(dependWrapper)) {
                return;
            }
        }
        dependWrappers.add(dependWrapper);
    }

    public void addNext(TaskWrapper<?, ?> workerWrapper) {
        if (nextWrappers == null) {
            nextWrappers = new ArrayList<>();
        }
        //避免添加重复
        for (TaskWrapper wrapper : nextWrappers) {
            if (workerWrapper.equals(wrapper)) {
                return;
            }
        }
        nextWrappers.add(workerWrapper);
    }

    private void addNextWrappers(List<TaskWrapper<?, ?>> wrappers) {
        if (wrappers == null) {
            return;
        }
        for (TaskWrapper<?, ?> wrapper : wrappers) {
            addNext(wrapper);
        }
    }

    private void addDependWrappers(List<DependWrapper> dependWrappers) {
        if (dependWrappers == null) {
            return;
        }
        for (DependWrapper wrapper : dependWrappers) {
            addDepend(wrapper);
        }
    }

    private TaskResult<V> defaultResult() {
        TaskResult<V> vTaskResult = TaskResult.defaultResult();
        vTaskResult.setResultState(ResultState.TIMEOUT);
        vTaskResult.setResult(worker.defaultValue());
        return vTaskResult;
    }

    private TaskResult<V> defaultExResult(Exception ex) {
        TaskResult<V> vTaskResult = TaskResult.defaultResult();
        vTaskResult.setResultState(ResultState.EXCEPTION);
        vTaskResult.setResult(worker.defaultValue());
        vTaskResult.setEx(ex);
        return vTaskResult;
    }


    private int getState(Long businessId) {
        Integer st = state.get(businessId);
        return st == null ? INIT : st;
    }

    public String getId() {
        return id;
    }

    public synchronized boolean compareAndSetState(int expect, int update, long businessId) {
        Integer st = this.state.get(businessId);
        if (st == null) {
            if (expect == INIT) {
                st = update;
                this.state.put(businessId, st);
                return true;
            }
            return false;
        }

        if (st == expect) {
            st = update;
            this.state.put(businessId, st);
            return true;
        }
        return false;
    }

    private void setNeedCheckNextWrapperResult(boolean needCheckNextWrapperResult) {
        this.needCheckNextWrapperResult = needCheckNextWrapperResult;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        TaskWrapper<?, ?> that = (TaskWrapper<?, ?>) o;
//        return needCheckNextWrapperResult == that.needCheckNextWrapperResult &&
//                Objects.equals(param, that.param) &&
//                Objects.equals(worker, that.worker) &&
//                Objects.equals(callback, that.callback) &&
//                Objects.equals(nextWrappers, that.nextWrappers) &&
//                Objects.equals(dependWrappers, that.dependWrappers) &&
//                Objects.equals(state.get(businessId), that.state.get(businessId)) &&
//                Objects.equals(workResult, that.workResult);
//    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(param, worker, callback, nextWrappers, dependWrappers, state.get(), workResult, needCheckNextWrapperResult);
//    }


    public static class Builder<W, C> {

        /**
         * 该wrapper的唯一标识
         */
        private String id = UUID.randomUUID().toString();
        /**
         * worker将来要处理的param
         */
        private W param;
        private ITask<W, C> worker;
        private ICallback<W, C> callback;
        /**
         * 自己后面的所有
         */
        private List<TaskWrapper<?, ?>> nextWrappers;
        /**
         * 自己依赖的所有
         */
        private List<DependWrapper> dependWrappers;
        /**
         * 存储强依赖于自己的wrapper集合
         */
        private Set<TaskWrapper<?, ?>> selfIsMustSet;

        // 默认true 需要检查 next  wrapper
        private boolean needCheckNextWrapperResult = true;

        public Builder<W, C> worker(ITask<W, C> worker) {
            this.worker = worker;
            return this;
        }

        public Builder<W, C> param(W w) {
            this.param = w;
            return this;
        }

        public Builder<W, C> id(String id) {
            if (id != null) {
                this.id = id;
            }
            return this;
        }

        public Builder<W, C> needCheckNextWrapperResult(boolean needCheckNextWrapperResult) {
            this.needCheckNextWrapperResult = needCheckNextWrapperResult;
            return this;
        }

        public Builder<W, C> callback(ICallback<W, C> callback) {
            this.callback = callback;
            return this;
        }

        public Builder<W, C> depend(TaskWrapper<?, ?>... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (TaskWrapper<?, ?> wrapper : wrappers) {
                depend(wrapper);
            }
            return this;
        }

        public Builder<W, C> depend(TaskWrapper<?, ?> wrapper) {
            return depend(wrapper, true);
        }

        public Builder<W, C> depend(TaskWrapper<?, ?> wrapper, boolean isMust) {
            if (wrapper == null) {
                return this;
            }
            DependWrapper dependWrapper = new DependWrapper(wrapper, isMust);
            if (dependWrappers == null) {
                dependWrappers = new ArrayList<>();
            }
            dependWrappers.add(dependWrapper);
            return this;
        }

        public Builder<W, C> next(TaskWrapper<?, ?> wrapper) {
            return next(wrapper, true);
        }

        public Builder<W, C> next(TaskWrapper<?, ?> wrapper, boolean selfIsMust) {
            if (nextWrappers == null) {
                nextWrappers = new ArrayList<>();
            }
            nextWrappers.add(wrapper);

            //强依赖自己
            if (selfIsMust) {
                if (selfIsMustSet == null) {
                    selfIsMustSet = new HashSet<>();
                }
                selfIsMustSet.add(wrapper);
            }
            return this;
        }

        public Builder<W, C> next(TaskWrapper<?, ?>... wrappers) {
            if (wrappers == null) {
                return this;
            }
            for (TaskWrapper<?, ?> wrapper : wrappers) {
                next(wrapper);
            }
            return this;
        }

        public TaskWrapper<W, C> build() {
            TaskWrapper<W, C> wrapper = new TaskWrapper(id, worker, param, callback);
            wrapper.setNeedCheckNextWrapperResult(needCheckNextWrapperResult);
            if (dependWrappers != null) {
                for (DependWrapper workerWrapper : dependWrappers) {
                    workerWrapper.getDependWrapper().addNext(wrapper);
                    wrapper.addDepend(workerWrapper);
                }
            }
            if (nextWrappers != null) {
                for (TaskWrapper<?, ?> workerWrapper : nextWrappers) {
                    boolean must = false;
                    if (selfIsMustSet != null && selfIsMustSet.contains(workerWrapper)) {
                        must = true;
                    }
                    workerWrapper.addDepend(wrapper, must);
                    wrapper.addNext(workerWrapper);
                }
            }

            return wrapper;
        }

    }
}
