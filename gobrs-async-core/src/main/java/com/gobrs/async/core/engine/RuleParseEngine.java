package com.gobrs.async.core.engine;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.TaskReceive;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.config.GobrsAsyncProperties;
import com.gobrs.async.core.common.def.Constant;
import com.gobrs.async.core.common.exception.GobrsAsyncException;
import com.gobrs.async.core.config.RuleConfig;
import com.gobrs.async.core.holder.BeanHolder;
import com.gobrs.async.core.common.def.DefaultConfig;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gobrs.async.core.common.def.DefaultConfig.*;

/**
 * The type Rule parse com.gobrs.async.engine.
 *
 * @param <T> the type parameter
 * @author sizegang1
 * @program: gobrs -async
 * @ClassName RuleParseEngine
 * @description:
 * @author: sizegang
 * @Version 1.0
 * @date 2022 -02-05 12:07
 */
public class RuleParseEngine<T> extends AbstractEngine {

    @Resource
    private GobrsAsyncProperties gobrsAsyncProperties;


    @Resource
    private GobrsAsync gobrsAsync;

    private static AtomicInteger current = new AtomicInteger(0);

    @Override
    public void doParse(RuleConfig rule, boolean reload) {

        String[] taskFlows = rule.getContent().replaceAll("\\s+", "").split(gobrsAsyncProperties.getSplit());
        /**
         * cache rules
         */
        Map<String, com.gobrs.async.core.task.AsyncTask> cacheTaskWrappers = new HashMap<>();

        List<com.gobrs.async.core.task.AsyncTask> pioneer = new ArrayList<>();

        for (String taskFlow : taskFlows) {

            String[] taskArr = taskFlow.split(gobrsAsyncProperties.getPoint());
            if (taskArr.length == 0) {
                throw new GobrsAsyncException("com.gobrs.async.rule com.gobrs.async.config error !!!");
            }
            String door = taskArr[0];
            if (door.contains(Constant.sp)) {
                String[] childFlows = door.split(Constant.sp);
                for (String cf : childFlows) {
                    com.gobrs.async.core.task.AsyncTask asyncTask = EngineExecutor.getAsyncTask(cf);
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
            String[] taskArr = taskFlow.split(gobrsAsyncProperties.getPoint());
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

    private void doChildFlow(TaskReceive taskReceive, Map<String, com.gobrs.async.core.task.AsyncTask> cacheTaskWrappers, List<String> arrayList) {
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
                List<com.gobrs.async.core.task.AsyncTask> asyncTasks = new ArrayList<>();
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
    public static class EngineExecutor {

        /**
         * Get assembly tasks
         *
         * @param taskName
         * @return
         */
        private static com.gobrs.async.core.task.AsyncTask getAsyncTask(String taskName) {
            String name = taskName;
            int cursor = 0;
            String[] preNamed = taskName.split(Constant.tied);
            if (taskName.contains(Constant.tied)) {
                String[] tiredNames = preNamed;
                name = tiredNames[0];
                cursor = tiredNames.length;
            }
            com.gobrs.async.core.task.AsyncTask task = (com.gobrs.async.core.task.AsyncTask) getBean(name);
            /**
             * Parse annotation configuration
             */
            task.setName(taskName);
            task.setDesc(getDesc(task));
            task.setCallback(getCallBack(task));
            task.setRetryCount(getRetryCount(task));
            task.setFailSubExec(getFailSubExec(task));
            if(!StringUtils.isEmpty(getName(task))){
                task.setName(getName(task));
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
        public static com.gobrs.async.core.task.AsyncTask getWrapperDepend(Map<String, com.gobrs.async.core.task.AsyncTask> cacheTaskWrappers, String taskBean, TaskReceive taskReceive,
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
                                com.gobrs.async.core.task.AsyncTask asyncTask = getAsyncTask(taskBean);
                                cacheTaskWrappers.put(taskBean, asyncTask);
                                /**
                                 * Set up subtasks
                                 */
                                taskReceive.then(clear, asyncTask);
                                return asyncTask;
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
         * Get the com.gobrs.async.com.gobrs.async.test.task name from the Task annotation
         *
         * @param task the com.gobrs.async.com.gobrs.async.test.task
         * @return name
         */
        public static String getDesc(com.gobrs.async.core.task.AsyncTask task) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (annotation == null) {
                return null;
            }
            return annotation.desc();
        }


        public static String getName(com.gobrs.async.core.task.AsyncTask task) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (annotation == null) {
                return null;
            }
            return annotation.value();
        }

        /**
         * transaction com.gobrs.async.com.gobrs.async.test.task
         *
         * @param task the com.gobrs.async.com.gobrs.async.test.task
         * @return call back
         */
        public static boolean getCallBack(com.gobrs.async.core.task.AsyncTask task) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (annotation == null) {
                return false;
            }
            return annotation.callback();
        }

        /**
         * com.gobrs.async.com.gobrs.async.test.task retries
         *
         * @param task the com.gobrs.async.com.gobrs.async.test.task
         * @return retry count
         */
        public static int getRetryCount(com.gobrs.async.core.task.AsyncTask task) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (annotation == null) {
                return DefaultConfig.retryCount;
            }
            return annotation.retryCount();
        }

        /**
         * Whether to continue the sub-process if the com.gobrs.async.com.gobrs.async.test.task execution fails
         *
         * @param task the com.gobrs.async.com.gobrs.async.test.task
         * @return fail sub exec
         */
        public static boolean getFailSubExec(com.gobrs.async.core.task.AsyncTask task) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (annotation == null) {
                return false;
            }
            return annotation.failSubExec();
        }


    }


}
