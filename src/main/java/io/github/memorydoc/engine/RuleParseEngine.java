package io.github.memorydoc.engine;

import io.github.memorydoc.autoconfig.GobrsAsyncProperties;
import io.github.memorydoc.callback.ICallback;
import io.github.memorydoc.callback.ITask;
import io.github.memorydoc.rule.Rule;
import io.github.memorydoc.spring.GobrsSpring;
import io.github.memorydoc.task.AsyncTask;
import io.github.memorydoc.wrapper.TaskWrapper;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Supplier;

/**
 * @program: gobrs-async
 * @ClassName RuleParseEngine
 * @description:
 * @author: sizegang
 * @Version 1.0
 **/
public class RuleParseEngine<T> extends AbstractEngine {

    @Resource
    private GobrsAsyncProperties gobrsAsyncProperties;

    public static final String DEFAULT_PARAMS = "default_params";

    @Override
    public Map<String, TaskWrapper> parsing(Rule rule, Map<String, Object> parameters) {
        String[] taskFlows = rule.getContent().replaceAll("\\s+", "").split(gobrsAsyncProperties.getSplit());
        Map<String, TaskWrapper> wrapperMap = new HashMap<>();
        for (String taskFlow : taskFlows) {
            String[] taskArr = taskFlow.split(gobrsAsyncProperties.getPoint());
            String leftTaskName = taskArr[0];
            TaskWrapper frontTaskWrapper = wrapperMap.get(leftTaskName);
            if (frontTaskWrapper == null) {
                frontTaskWrapper = EngineExecutor.getWrapper(leftTaskName);
                EngineExecutor.setParams(frontTaskWrapper, parameters);
                wrapperMap.put(leftTaskName, frontTaskWrapper);
            }
            for (int i = 1; i < taskArr.length; i++) {
                String taskBean = taskArr[i];
                if (taskBean.contains(gobrsAsyncProperties.getMust())) { // 强以来上游 上游不返回 方法不执行
                    taskBean = taskBean.replace(gobrsAsyncProperties.getMust(), "");
                    frontTaskWrapper = EngineExecutor.getWrapperDepend(taskBean, frontTaskWrapper, false);
                } else {
                    frontTaskWrapper = EngineExecutor.getWrapperDepend(taskBean, frontTaskWrapper);
                }
                EngineExecutor.setParams(frontTaskWrapper, parameters);
            }
        }

        return wrapperMap;
    }

    /**
     * 参数解析
     *
     * @return
     */
    @Override
    public Map<String, TaskWrapper> invokeParam(Map<String, TaskWrapper> wrapperMap, Object parameter) {

        return null;
    }

    @Override
    public Map<String, TaskWrapper> invokeParamsSupplier(Map<String, TaskWrapper> t, Supplier<Map<String, Object>> supplier) {
        return null;
    }

    public Rule getRule(String key) {
        return super.ruleMap.get(key);
    }

    public static class EngineExecutor {
        private static TaskWrapper getWrapper(String s3) {
            return new TaskWrapper.Builder()
                    .worker((AsyncTask) getBean(s3))
                    .callback((ICallback) getBean(s3)).build();
        }

        private static TaskWrapper getWrapperDepend(String taskBean, TaskWrapper taskWrapper) {
            return getWrapperDepend(taskBean, taskWrapper, true);
        }

        private static TaskWrapper getWrapperDepend(String taskBean, TaskWrapper taskWrapper, boolean must) {
            return Optional.ofNullable(getBean(taskBean)).map((bean) -> {
                return new TaskWrapper.Builder()
                        .id(taskBean)
                        .worker((AsyncTask) bean)
                        .callback((ICallback) bean)
                        .depend(taskWrapper, must)
                        .build();
            }).orElse(new TaskWrapper.Builder<>().build());
        }

        public static Object getBean(String bean) {
            return Optional.ofNullable(GobrsSpring.getBean(bean)).orElseThrow(() -> new RuntimeException("bean not found"));
        }

        public static void setParams(TaskWrapper taskWrapper, Map<String, Object> paramMap) {
            if (paramMap.size() == 1 && paramMap.containsKey(DEFAULT_PARAMS)) {
                taskWrapper.setParam(paramMap.get(DEFAULT_PARAMS));
            } else {
                taskWrapper.setParam(paramMap.get(taskWrapper.getId()));
            }
        }
    }


    public static void main(String[] args) {
        //String rule = "A->B->F->H;A->C->F->H;D->E->G->H"
//        String rule = "A->B:must->D->F;A->C->E->F";
        //String rule = "A->B->F->H;A->C->F->H;D->E->G->H";
//        String rule = "A;B;C";

//        String rule = "A->B;A->C:not";

//        String rule = "B->A;C->A:not";
        String rule = "A->B->F:must->H; A->C->F->H; D->E->G->H; c; d; f; ";
        Rule r = new Rule();
        r.setName("test");
        r.setContent(rule);
        RuleParseEngine ruleParseEngine = new RuleParseEngine();
//        ruleParseEngine.parsing(r);
//        System.out.println(JSONObject.toJSONString(taskWraMap));
    }


}
