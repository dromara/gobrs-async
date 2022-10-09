package com.gobrs.async.test;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.domain.TaskResult;
import com.gobrs.async.example.GobrsAsyncExampleApplication;
import com.gobrs.async.example.task.AService;
import com.gobrs.async.example.task.CService;
import com.gobrs.async.example.task.condition.AServiceCondition;
import com.gobrs.async.example.task.condition.CServiceCondition;
import com.gobrs.async.threadpool.GobrsThreadLocal;
import org.junit.jupiter.api.Test;
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
 * @program: gobrs-async
 * @ClassName ThreadLocalTest
 * @description:
 * @author: sizegang
 * @create: 2022-10-09
 **/
@SpringBootTest(classes = GobrsAsyncExampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ThreadLocalTest {

    @Autowired(required = false)
    private GobrsAsync gobrsAsync;

    public Integer count = 1000;

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void threadLocalTest(){
        Set<String> cases = new HashSet<>();
        cases.add("BService");
        cases.add("GService");

        Map<Class, Object> params = new HashMap<>();
        params.put(AService.class, "1");
        params.put(CService.class, "2");

        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                AsyncResult result = gobrsAsync.go("general", () -> params, 10000);
                stopWatch.stop();
                System.out.println(stopWatch.getTotalTimeMillis());
                TaskResult taskResult = result.getResultMap().get(CServiceCondition.class);
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("【gobrs-async】 testCondition 执行完成");

    }

}
