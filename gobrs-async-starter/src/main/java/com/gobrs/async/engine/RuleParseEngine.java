package com.gobrs.async.engine;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.TaskReceive;
import com.gobrs.async.anno.Task;
import com.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.gobrs.async.def.DefaultConfig;
import com.gobrs.async.exception.GobrsAsyncException;
import com.gobrs.async.rule.Rule;
import com.gobrs.async.spring.GobrsSpring;
import com.gobrs.async.task.AsyncTask;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author sizegang1
 * @program: gobrs-async
 * @ClassName RuleParseEngine
 * @description:
 * @author: sizegang
 * @Version 1.0
 * @date 2022-02-05 12:07
 **/
public class RuleParseEngine<T> extends AbstractEngine {

    @Resource
    private GobrsAsyncProperties gobrsAsyncProperties;


    public static final String sp = ",";

    @Resource
    private GobrsAsync gobrsAsync;

    @Override
    public void doParse(Rule rule, boolean reload) {

        String[] taskFlows = rule.getContent().replaceAll("\\s+", "").split(gobrsAsyncProperties.getSplit());
        /**
         * cache rules
         */
        Map<String, AsyncTask> cacheTaskWrappers = new HashMap<>();

        List<AsyncTask> pioneer = new ArrayList<>();

        for (String taskFlow : taskFlows) {
            String[] taskArr = taskFlow.split(gobrsAsyncProperties.getPoint());
            if (taskArr.length == 0) {
                throw new GobrsAsyncException("rule config error !!!");
            }
            String door = taskArr[0];
            if (door.contains(sp)) {
                String[] childFlows = door.split(sp);
                for (String cf : childFlows) {
                    pioneer.add(EngineExecutor.getAsyncTask(cf));
                }

            } else {
                pioneer.add(EngineExecutor.getAsyncTask(door));
            }
        }
        /**
         * Start the task process The sub-process in the task process is opened
         */
        gobrsAsync.begin(rule.getName(), pioneer, reload);

        for (String taskFlow : taskFlows) {
            /**
             * Parse tasks according to parsing rules
             */
            String[] taskArr = taskFlow.split(gobrsAsyncProperties.getPoint());
            List<String> arrayList = Arrays.asList(taskArr);
            String leftTaskName = arrayList.get(0);
            if (leftTaskName.contains(sp)) {
                String[] split = leftTaskName.split(sp);
                for (String s : split) {
                    TaskReceive taskReceive = gobrsAsync.after(rule.getName(), EngineExecutor.getAsyncTask(s));
                    doChildFlow(taskReceive, cacheTaskWrappers, arrayList);
                }

            } else {
                /**
                 * Set up subtasks Task tree
                 */
                TaskReceive taskReceive = gobrsAsync.after(rule.getName(), EngineExecutor.getAsyncTask(leftTaskName));
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
            if (taskBean.contains(sp)) {

                String[] beanArray = taskBean.split(sp);

                List<String> beanList = Arrays.asList(beanArray);
                /**
                 * Load tasks from the rules engine
                 */
                for (String tbean : beanList) {

                    EngineExecutor.getWrapperDepend(cacheTaskWrappers, tbean, taskReceive, true);
                }

            } else {

                EngineExecutor.getWrapperDepend(cacheTaskWrappers, taskBean, taskReceive, false);
            }
        }
    }


    /**
     * The rule engine executor is mainly responsible for obtaining tasks and setting task processes
     */
    public static class EngineExecutor {

        /**
         * Get assembly tasks
         *
         * @param taskName
         * @return
         */
        private static AsyncTask getAsyncTask(String taskName) {
            AsyncTask task = (AsyncTask) getBean(taskName);
            /**
             * Parse annotation configuration
             */
            task.setName(getName(task));
            task.setCallback(getCallBack(task));
            task.setRetryCount(getRetryCount(task));
            task.setFailSubExec(getFailSubExec(task));
            task.setAny(isAny(task));
            return task;
        }


        /**
         * Get packaging tasks
         *
         * @param cacheTaskWrappers
         * @param taskBean
         * @param taskReceive
         * @return
         */
        public static AsyncTask getWrapperDepend(Map<String, AsyncTask> cacheTaskWrappers, String taskBean, TaskReceive taskReceive,
                                                 boolean split) {
            /**
             *  parsing task rule configuration
             */
            return Optional.ofNullable(getAsyncTask(taskBean))
                    .map((bean) -> Optional.ofNullable(cacheTaskWrappers.get(taskBean))
                            .map((tk) -> {
                                taskReceive.then(split, tk);
                                return tk;
                            }).orElseGet(() -> {
                                /**
                                 * load task
                                 */
                                AsyncTask asyncTask = getAsyncTask(taskBean);
                                cacheTaskWrappers.put(taskBean, asyncTask);
                                /**
                                 * Set up subtasks
                                 */
                                taskReceive.then(split, asyncTask);
                                return asyncTask;
                            })).orElse(null);
        }

        public static Object getBean(String bean) {
            return Optional.ofNullable(GobrsSpring.getBean(bean)).orElseThrow(() -> new RuntimeException("bean not found"));
        }

        /**
         * Get the task name from the Task annotation
         *
         * @param task
         * @return
         */
        public static String getName(AsyncTask task) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (annotation == null) {
                return null;
            }
            return annotation.name();
        }

        /**
         * transaction task
         *
         * @param task
         * @return
         */
        public static boolean getCallBack(AsyncTask task) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (annotation == null) {
                return false;
            }
            return annotation.callback();
        }

        /**
         * task retries
         *
         * @param task
         * @return
         */
        public static int getRetryCount(AsyncTask task) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (annotation == null) {
                return DefaultConfig.retryCount;
            }
            return annotation.retryCount();
        }

        /**
         * Whether to continue the sub-process if the task execution fails
         *
         * @param task
         * @return
         */
        public static boolean getFailSubExec(AsyncTask task) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (annotation == null) {
                return false;
            }
            return annotation.failSubExec();
        }

        /**
         * if true => execute when any of parentTasks finished
         * @param task
         * @return
         */
        public static boolean isAny(AsyncTask task) {
            Task annotation = task.getClass().getAnnotation(Task.class);
            if (annotation == null) {
                return false;
            }
            return annotation.any();
        }
    }

}
