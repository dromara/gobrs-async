package com.gobrs.async.core.engine;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.TaskReceive;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.common.def.Constant;
import com.gobrs.async.core.common.exception.GobrsAsyncException;
import com.gobrs.async.core.config.GobrsAsyncRule;
import com.gobrs.async.core.config.GobrsConfig;
import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.core.task.AsyncTask;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;

import static com.gobrs.async.core.common.def.DefaultConfig.*;

/**
 * The type Rule parse com.gobrs.async.engine.
 *
 * @author sizegang1
 * @program: gobrs -async
 * @ClassName RuleParseEngine
 * @description:
 * @author: sizegang
 * @Version 1.0
 * @date 2022 -02-05 12:07
 */
public class RuleParseEngine extends AbstractEngine {

    private GobrsConfig gobrsConfig;

    private GobrsAsync gobrsAsync;


    /**
     * Instantiates a new Rule parse engine.
     *
     * @param gobrsConfig the gobrs config
     * @param gobrsAsync  the gobrs async
     */
    public RuleParseEngine(GobrsConfig gobrsConfig, GobrsAsync gobrsAsync) {
        this.gobrsConfig = gobrsConfig;
        this.gobrsAsync = gobrsAsync;
    }

    @Override
    public void doParse(GobrsAsyncRule rule, boolean reload) {

        String[] taskFlows = rule.getContent().replaceAll("\\s+", "").split(gobrsConfig.getSplit());
        /**
         * cache rules
         */
        Map<String, AsyncTask> cacheTaskWrappers = new HashMap<>();

        List<AsyncTask<?, ?>> pioneer = new ArrayList<>();

        for (String taskFlow : taskFlows) {

            String[] taskArr = taskFlow.split(gobrsConfig.getPoint());
            if (taskArr.length == 0) {
                throw new GobrsAsyncException("com.gobrs.async.rule com.gobrs.async.config error !!!");
            }
            String door = taskArr[0];
            if (door.contains(Constant.sp)) {
                String[] childFlows = door.split(Constant.sp);
                for (String cf : childFlows) {
                    AsyncTask<?, ?> asyncTask = EngineExecutor.getAsyncTask(cf);
                    pioneer.add(asyncTask);
                }
            } else {
                pioneer.add(EngineExecutor.getAsyncTask(door));
            }
        }
        /**
         * Start the com.gobrs.async.com.gobrs.async.test.task process The sub-process in the com.gobrs.async.com.gobrs.async.test.task process is opened
         */
        gobrsAsync.begin(rule.getName(), pioneer, reload);

        for (String taskFlow : taskFlows) {
            /**
             * Parse tasks according to parsing rules
             */
            String[] taskArr = taskFlow.split(gobrsConfig.getPoint());
            List<String> arrayList = Arrays.asList(taskArr);
            String leftTaskName = arrayList.get(0);
            TaskReceive taskReceive;
            if (leftTaskName.contains(Constant.sp)) {
                String[] split = leftTaskName.split(Constant.sp);
                for (String s : split) {
                    taskReceive = gobrsAsync.after(rule.getName(), EngineExecutor.getAsyncTask(s));
                    doChildFlow(taskReceive, cacheTaskWrappers, arrayList);
                }
            } else {
                /**
                 * Set up subtasks Task tree
                 */
                taskReceive = gobrsAsync.after(rule.getName(), EngineExecutor.getAsyncTask(leftTaskName));
                doChildFlow(taskReceive, cacheTaskWrappers, arrayList);
            }
        }
    }

    private void doChildFlow(TaskReceive taskReceive, Map<String, AsyncTask> cacheTaskWrappers, List<String> arrayList) {
        for (int i = 1; i < arrayList.size(); i++) {

            String taskBean = arrayList.get(i);

            /**
             * Parse Task Rules
             */
            if (taskBean.contains(Constant.sp)) {

                String[] beanArray = taskBean.split(Constant.sp);

                List<String> beanList = Arrays.asList(beanArray);
                /**
                 * Load tasks from the rules com.gobrs.async.engine
                 */
                List<AsyncTask> asyncTasks = new ArrayList<>();
                for (String tbean : beanList) {
                    asyncTasks.add(EngineExecutor.getWrapperDepend(cacheTaskWrappers, tbean, taskReceive, false));
                }
                taskReceive.refresh(asyncTasks);

            } else {

                EngineExecutor.getWrapperDepend(cacheTaskWrappers, taskBean, taskReceive, true);
            }
        }
    }


    /**
     * The com.gobrs.async.rule com.gobrs.async.engine executor is mainly responsible for obtaining tasks and setting com.gobrs.async.com.gobrs.async.test.task processes
     */
    static class EngineExecutor {

        /**
         * Get assembly tasks
         *
         * @param taskName
         * @return
         */
        private static AsyncTask<?, ?> getAsyncTask(String taskName) {
            String name = taskName;
            int cursor = 0;
            String[] preNamed = taskName.split(Constant.tied);
            if (taskName.contains(Constant.tied)) {
                String[] tiredNames = preNamed;
                name = tiredNames[0];
                cursor = tiredNames.length;
            }
            AsyncTask<?, ?> task = (AsyncTask<?, ?>) getBean(name);
            /**
             * Parse annotation configuration
             */

            task.setDesc(getTaskAnnotion(task, taskName, (anno) -> anno.desc(), String.class));
            task.setDesc(getTaskAnnotion(task, taskName, (anno) -> anno.desc(), String.class));
            task.setCallback(getTaskAnnotion(task, taskName, (anno) -> anno.callback(), Boolean.class));
            task.setRetryCount(getTaskAnnotion(task, taskName, (anno) -> anno.retryCount(), Integer.class));
            task.setFailSubExec(getTaskAnnotion(task, taskName, (anno) -> anno.failSubExec(), Boolean.class));
            task.setTimeoutInMilliseconds(getTaskAnnotion(task, taskName, (anno) -> anno.timeoutInMilliseconds(), Integer.class));
            String annotionTaskName = getTaskAnnotion(task, taskName, (anno) -> anno.desc(), String.class);

            if (!StringUtils.isEmpty(annotionTaskName)) {
                task.setName(annotionTaskName);
            } else {
                task.setName(taskName);
            }

            if (taskName.contains(Constant.tied) && RULE_ANY.equals(preNamed[1])) {
                task.setAny(true);
            }

            if (taskName.contains(Constant.tied) && RULE_ANY_CONDITION.equals(preNamed[1])) {
                task.setAnyCondition(true);
            }

            if (cursor == 3 && RULE_EXCLUSIVE.equals(preNamed[2])) {
                task.setExclusive(true);
            }
            return task;
        }


        /**
         * Get packaging tasks
         *
         * @param cacheTaskWrappers the cache com.gobrs.async.com.gobrs.async.test.task wrappers
         * @param taskBean          the com.gobrs.async.com.gobrs.async.test.task bean
         * @param taskReceive       the com.gobrs.async.com.gobrs.async.test.task receive
         * @param clear             the clear
         * @return wrapper depend
         */
        public static AsyncTask getWrapperDepend(Map<String, AsyncTask> cacheTaskWrappers, String taskBean, TaskReceive taskReceive,
                                                       boolean clear) {
            /**
             *  parsing com.gobrs.async.com.gobrs.async.test.task com.gobrs.async.rule configuration
             */
            return Optional.ofNullable(getAsyncTask(taskBean))
                    .map((bean) -> Optional.ofNullable(cacheTaskWrappers.get(taskBean))
                            .map((tk) -> {
                                taskReceive.then(clear, tk);
                                return tk;
                            }).orElseGet(() -> {
                                /**
                                 * load com.gobrs.async.com.gobrs.async.test.task
                                 */
                                AsyncTask asyncTask = getAsyncTask(taskBean);
                                cacheTaskWrappers.put(taskBean, asyncTask);
                                /**
                                 * Set up subtasks
                                 */
                                taskReceive.then(clear, asyncTask);
                                return  asyncTask;
                            })).orElse(null);
        }

        /**
         * Gets bean.
         *
         * @param bean the bean
         * @return the bean
         */
        public static Object getBean(String bean) {
            return Optional.ofNullable(BeanHolder.getBean(bean)).orElseThrow(() -> new RuntimeException("bean not found, name is " + bean));
        }

        /**
         * @param task
         * @param function
         * @param tClass
         * @param <T>
         * @return
         */
        private static <T> T getTaskAnnotion(AsyncTask<?, ?> task, String taskName, Function<Task, T> function, Class<T> tClass) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (Objects.isNull(annotation)) {
                throw new GobrsAsyncException(String.format("Tasks %s are not annotated with @Task", taskName));
            }
            return function.apply(annotation);
        }

    }


}
