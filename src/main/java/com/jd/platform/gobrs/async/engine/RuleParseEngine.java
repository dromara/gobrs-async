package com.jd.platform.gobrs.async.engine;

import com.jd.platform.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.jd.platform.gobrs.async.callback.ICallback;
import com.jd.platform.gobrs.async.callback.ITask;
import com.jd.platform.gobrs.async.rule.Rule;
import com.jd.platform.gobrs.async.spring.GobrsSpring;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @program: gobrs-async
 * @ClassName RuleParseEngine
 * @description:
 * @author: sizegang
 * @create: 2022-01-26 01:55
 * @Version 1.0
 **/
public class RuleParseEngine extends AbstractEngine {

    @Resource
    private GobrsAsyncProperties gobrsAsyncProperties;

    /**
     * 树形结构 折叠 taskWrapper
     */
    public Map<String, List<TaskWrapper>> taskWraMap = new ConcurrentHashMap<>();

    /**
     * 保存所有的rule命名空间下的 Wrapper 平铺
     */
    public Map<String, Map<String, TaskWrapper>> flatWrapMap = new ConcurrentHashMap<>();


    public Object getBean(String bean) {
        return Optional.ofNullable(GobrsSpring.getBean(bean)).orElseThrow(() -> {
            return null;
        });
    }


    @Override
    public List<TaskWrapper> parsing(Rule rule) {
        String[] taskFlows = rule.getContent().replaceAll("\\s+", "").split(gobrsAsyncProperties.getSplit());
        Map<String, TaskWrapper> wrapperMap = new HashMap<>();
        Map<String, TaskWrapper> flatWrap = new HashMap<>();
        for (String taskFlow : taskFlows) {
            String[] taskArr = taskFlow.split(gobrsAsyncProperties.getPoint());
            String leftTaskName = taskArr[0];
            TaskWrapper frontTaskWrapper = wrapperMap.get(leftTaskName);
            if (frontTaskWrapper == null) {
                frontTaskWrapper = getWrapper(leftTaskName);
                wrapperMap.put(leftTaskName, frontTaskWrapper);
                flatWrap.put(leftTaskName, frontTaskWrapper);
            }
            for (int i = 1; i < taskArr.length; i++) {
                String taskBean = taskArr[i];
                if (taskBean.contains(gobrsAsyncProperties.getMust())) { // 强以来上游 上游不返回 方法不执行
                    taskBean = taskBean.replace(gobrsAsyncProperties.getMust(), "");
                    frontTaskWrapper = getWrapperDepend(taskBean, frontTaskWrapper, false);
                } else {
                    frontTaskWrapper = getWrapperDepend(taskBean, frontTaskWrapper);
                }
                flatWrap.put(taskBean, frontTaskWrapper);
            }
        }
        List<TaskWrapper> collect = wrapperMap.values().stream().collect(Collectors.toList());
        taskWraMap.put(rule.getName(), collect);
        flatWrapMap.put(rule.getName(), flatWrap);
        return null;
    }


    private TaskWrapper getWrapper(String s3) {
        return new TaskWrapper.Builder()
                .worker((ITask) getBean(s3))
                .callback((ICallback) getBean(s3)).build();
    }


    private TaskWrapper getWrapperDepend(String taskBean, TaskWrapper taskWrapper) {
        return getWrapperDepend(taskBean, taskWrapper, true);
    }

    private TaskWrapper getWrapperDepend(String taskBean, TaskWrapper taskWrapper, boolean must) {
        return Optional.ofNullable(getBean(taskBean)).map((bean) -> {
            return new TaskWrapper.Builder()
                    .worker((ITask) bean)
                    .callback((ICallback) bean)
                    .depend(taskWrapper, must)
                    .build();
        }).orElse(new TaskWrapper.Builder<>().build());
    }


    public static void main(String[] args) {
        //String rule = "A->B->F->H;A->C->F->H;D->E->G->H"
//        String rule = "A->B:must->D->F;A->C->E->F";
        //String rule = "A->B->F->H;A->C->F->H;D->E->G->H";
//        String rule = "A;B;C";

//        String rule = "A->B;A->C:not";

//        String rule = "B->A;C->A:not";
        String rule = "A->B; A->C; A->D;A->F; A->H ";

        Rule r = new Rule();
        r.setName("test");
        r.setContent(rule);
        RuleParseEngine ruleParseEngine = new RuleParseEngine();
        ruleParseEngine.parsing(r);
//        System.out.println(JSONObject.toJSONString(taskWraMap));
    }


}
