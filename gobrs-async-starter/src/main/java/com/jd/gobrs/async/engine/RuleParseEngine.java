package com.jd.gobrs.async.engine;

import com.jd.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.jd.gobrs.async.callback.ICallback;
import com.jd.gobrs.async.executor.Async;
import com.jd.gobrs.async.spring.GobrsSpring;
import com.jd.gobrs.async.task.DependWrapper;
import com.jd.gobrs.async.rule.Rule;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.wrapper.TaskWrapper;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    @Override
    public Map<String, TaskWrapper> doParse(Rule rule, Map<String, Object> parameters) {
        String[] taskFlows = rule.getContent().replaceAll("\\s+", "").split(gobrsAsyncProperties.getSplit());
        Map<String, TaskWrapper> wrapperMap = new HashMap<>();
        Map<String, TaskWrapper> cacheTaskWrappers = new HashMap<>();
        for (String taskFlow : taskFlows) {
            String[] taskArr = taskFlow.split(gobrsAsyncProperties.getPoint());
            List<String> arrayList = Arrays.asList(taskArr);
            String leftTaskName = arrayList.get(0);
            TaskWrapper frontTaskWrapper = wrapperMap.get(leftTaskName);
            if (frontTaskWrapper == null) {
                frontTaskWrapper = EngineExecutor.getWrapper(leftTaskName);
                wrapperMap.put(leftTaskName, frontTaskWrapper);
            }
            for (int i = 1; i < arrayList.size(); i++) {
                String taskBean = arrayList.get(i);
                if (taskBean.contains(sp)) {
                    String[] beanArray = taskBean.split(sp);
                    List<String> beanList = Arrays.asList(beanArray);
                    TaskWrapper taskWrapper = null;
                    for (String b : beanList) {
                        if (b.contains(gobrsAsyncProperties.getMust())) { // 强依赖上游 上游不返回 方法不执行
                            b = b.replace(gobrsAsyncProperties.getMust(), "");
                            taskWrapper = EngineExecutor.getWrapperDepend(cacheTaskWrappers, b, frontTaskWrapper, false);
                        } else {
                            taskWrapper = EngineExecutor.getWrapperDepend(cacheTaskWrappers, b, frontTaskWrapper);
                        }
                    }
                    frontTaskWrapper = taskWrapper;
                } else {
                    if (taskBean.contains(gobrsAsyncProperties.getMust())) { // 强依赖上游 上游不返回 方法不执行
                        taskBean = taskBean.replace(gobrsAsyncProperties.getMust(), "");
                        frontTaskWrapper = EngineExecutor.getWrapperDepend(cacheTaskWrappers, taskBean, frontTaskWrapper, false);
                    } else {
                        frontTaskWrapper = EngineExecutor.getWrapperDepend(cacheTaskWrappers, taskBean, frontTaskWrapper);
                    }
                }
            }
        }

        return wrapperMap;
    }

    @Override
    public boolean exec(String ruleName, Supplier<Map<String, Object>> supplier, long timeout) throws ExecutionException, InterruptedException {
        return Async.startTaskFlow(timeout,
                taskRuleMap.get(ruleName).values().parallelStream().collect(Collectors.toList()), supplier.get());
    }

    public Rule getRule(String key) {
        return super.ruleMap.get(key);
    }

    public static class EngineExecutor {
        private static TaskWrapper getWrapper(String taskName) {
            return new TaskWrapper.Builder()
                    .id(taskName)
                    .worker((AsyncTask) getBean(taskName))
                    .callback((ICallback) getBean(taskName)).build();
        }

        private static TaskWrapper getWrapperDepend(Map<String, TaskWrapper> cacheTaskWrappers, String taskBean, TaskWrapper taskWrapper) {
            return getWrapperDepend(cacheTaskWrappers, taskBean, taskWrapper, true);
        }

        private static TaskWrapper getWrapperDepend(Map<String, TaskWrapper> cacheTaskWrappers, String taskBean, TaskWrapper taskWrapper, boolean must) {
            return Optional.ofNullable(getBean(taskBean)).map((bean) -> Optional.ofNullable(cacheTaskWrappers.get(taskBean)).map((tk) -> {
                List<DependWrapper> dependWrappers = tk.getDependWrappers();
                DependWrapper dependWrapper = new DependWrapper(taskWrapper, must);
                if (dependWrappers != null) {
                    dependWrappers.add(dependWrapper);
                } else {
                    dependWrappers = new ArrayList();
                    dependWrappers.add(dependWrapper);
                    tk.addDepend(dependWrapper);
                }
                tk.setDependWrappers(dependWrappers);
                taskWrapper.addNext(tk);
                return tk;
            }).orElseGet(() -> {
                TaskWrapper tk = new TaskWrapper.Builder()
                        .id(taskBean)
                        .worker((AsyncTask) bean)
                        .callback((ICallback) bean)
                        .depend(taskWrapper, must)
                        .build();
                cacheTaskWrappers.put(taskBean, tk);
                return tk;
            })).orElse(null);
        }

        public static Object getBean(String bean) {
            return Optional.ofNullable(GobrsSpring.getBean(bean)).orElseThrow(() -> new RuntimeException("bean not found"));
        }

//        public static void setParams(TaskWrapper taskWrapper, Map<String, Object> paramMap) {
//            if (paramMap.size() == 1 && paramMap.containsKey(DEFAULT_PARAMS)) {
//                taskWrapper.setParam(paramMap.get(DEFAULT_PARAMS));
//            } else {
//                taskWrapper.setParam(paramMap.get(taskWrapper.getId()));
//            }
//        }
    }

}
