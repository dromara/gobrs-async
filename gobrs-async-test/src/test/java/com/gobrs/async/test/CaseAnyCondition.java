package com.gobrs.async.test;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.core.common.domain.TaskResult;
import com.gobrs.async.test.task.condition.AServiceCondition;
import com.gobrs.async.test.task.condition.BServiceCondition;
import com.gobrs.async.test.task.condition.CServiceCondition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Case any condition.
 *
 * @program: gobrs -async
 * @ClassName CaseAnyCondition
 * @description:
 * @author: sizegang
 * @create: 2022 -09-28
 */
@SpringBootTest(classes = GobrsAsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseAnyCondition {

    @Autowired(required = false)
    private GobrsAsync gobrsAsync;

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public Integer count = 1;

    /**
     * Test condition.
     */
    @Test
    public void testCondition() {

        Map<Class, Object> params = new HashMap<>();
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                AsyncResult result = gobrsAsync.go("anyConditionRule", () -> params, 10000);
                stopWatch.stop();
                System.out.println(stopWatch.getTotalTimeMillis());
                TaskResult taskResult = result.getResultMap().get(CServiceCondition.class);
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("【gobrs-async】 testCondition 执行完成");

    }

    @Test
    public void testConditionAppend() {
        Set<String> cases = new HashSet<>();
        cases.add("BService");
        cases.add("GService");

        Map<Class, Object> params = new HashMap<>();
        params.put(AServiceCondition.class, "1");
        params.put(CServiceCondition.class, "2");

        AsyncResult result = gobrsAsync.go("anyConditionRuleAppend", () -> params, 300000);

        TaskResult aResult = result.getResultMap().get(AServiceCondition.class);
        TaskResult bResult = result.getResultMap().get(BServiceCondition.class);
        TaskResult cResult = result.getResultMap().get(CServiceCondition.class);

    }
}
