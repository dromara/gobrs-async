package com.jd.gobrs.async.test;

import com.jd.gobrs.async.example.GobrsAsyncExampleApplication;
import com.jd.gobrs.async.example.service.GobrsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: gobrs-async
 * @ClassName GobrsTest
 * @description:
 * @author: sizegang
 * @create: 2022-02-26 16:49
 * @Version 1.0
 **/
@SpringBootTest
@ContextConfiguration(classes = GobrsAsyncExampleApplication.class)
public class GobrsTest {

    private static int cyc = 1000;


    private static int th = 100;
    @Autowired
    private GobrsService gobrsService;


    public ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void testGobrsAsync() {
        for (int i = 0; i < cyc; i++) {
            CountDownLatch countDownLatch = new CountDownLatch(th);
            doAsync(countDownLatch);
        }
    }


    @Test
    public void testFutures() {
        for (int i = 0; i < cyc; i++) {
            CountDownLatch countDownLatch = new CountDownLatch(th);
            doFutures(countDownLatch);
        }
    }


    private void doAsync(CountDownLatch countDownLatch) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < th; i++) {
            executorService.execute(() -> {
                gobrsService.testGobrs();
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long costTime = System.currentTimeMillis() - startTime;
        System.out.println("gobrs-async cost time -> " + costTime);
    }


    private void doFutures(CountDownLatch countDownLatch) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < th; i++) {
            executorService.execute(() -> {
                gobrsService.testFuture();
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long costTime = System.currentTimeMillis() - startTime;
        System.out.println("future cost time -> " + costTime);
    }
}
