package com.gobrs.async.engine;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.TaskRecevieFlow;
import com.gobrs.async.autoconfig.GobrsAsyncProperties;
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
    public void doParse(Rule rule, Map<String, Object> parameters) {
        String[] taskFlows = rule.getContent().replaceAll("\\s+", "").split(gobrsAsyncProperties.getSplit());
        Map<String, AsyncTask> cacheTaskWrappers = new HashMap<>();
        List<AsyncTask> pioneer = new ArrayList<>();
        for (String taskFlow : taskFlows) {
            String[] taskArr = taskFlow.split(gobrsAsyncProperties.getPoint());
            pioneer.add(EngineExecutor.getAsyncTask(taskArr[0]));
        }
        gobrsAsync.begin(rule.getName(),pioneer);
        for (String taskFlow : taskFlows) {
            String[] taskArr = taskFlow.split(gobrsAsyncProperties.getPoint());
            List<String> arrayList = Arrays.asList(taskArr);
            String leftTaskName = arrayList.get(0);
            TaskRecevieFlow taskBuilder = gobrsAsync.after(rule.getName(),EngineExecutor.getAsyncTask(leftTaskName));
            for (int i = 1; i < arrayList.size(); i++) {
                String taskBean = arrayList.get(i);
                if (taskBean.contains(sp)) {
                    String[] beanArray = taskBean.split(sp);
                    List<String> beanList = Arrays.asList(beanArray);
                    for (String tbean : beanList) {
                        if (tbean.contains(gobrsAsyncProperties.getMust())) { // 强依赖上游 上游不返回 方法不执行
                            tbean = tbean.replace(gobrsAsyncProperties.getMust(), "");
                        } else {
                            EngineExecutor.getWrapperDepend(cacheTaskWrappers, tbean, taskBuilder);
                        }
                    }
                } else {
                    if (taskBean.contains(gobrsAsyncProperties.getMust())) { // 强依赖上游 上游不返回 方法不执行
                        taskBean = taskBean.replace(gobrsAsyncProperties.getMust(), "");
                        EngineExecutor.getWrapperDepend(cacheTaskWrappers, taskBean, taskBuilder, false);
                    } else {
                        EngineExecutor.getWrapperDepend(cacheTaskWrappers, taskBean, taskBuilder);
                    }
                }
            }
        }
    }


    public static class EngineExecutor {
        private static AsyncTask getAsyncTask(String taskName) {
            return (AsyncTask) getBean(taskName);
        }

        public static AsyncTask getWrapperDepend(Map<String, AsyncTask> cacheTaskWrappers, String taskBean, TaskRecevieFlow taskBuilder) {
            return getWrapperDepend(cacheTaskWrappers, taskBean, taskBuilder, true);
        }

        public static AsyncTask getWrapperDepend(Map<String, AsyncTask> cacheTaskWrappers, String taskBean, TaskRecevieFlow taskBuilder, boolean must) {
            return Optional.ofNullable(getAsyncTask(taskBean)).map((bean) -> Optional.ofNullable(cacheTaskWrappers.get(taskBean)).map((tk) -> {
                taskBuilder.then(tk);
                return tk;
            }).orElseGet(() -> {
                AsyncTask asyncTask = getAsyncTask(taskBean);
                cacheTaskWrappers.put(taskBean, asyncTask);
                taskBuilder.then(asyncTask);
                return asyncTask;
            })).orElse(null);
        }

        public static Object getBean(String bean) {
            return Optional.ofNullable(GobrsSpring.getBean(bean)).orElseThrow(() -> new RuntimeException("bean not found"));
        }
    }

}
