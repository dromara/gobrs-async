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

    private static int th = 100;
    @Autowired
    private GobrsService gobrsService;
    CountDownLatch countDownLatch = new CountDownLatch(th);

    public ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void testGobrsAsync(){
        long startTime = System.currentTimeMillis();
        for (int i =0; i< th; i++){
            gobrsService.testGobrs();
            executorService.execute(()->{
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



    @Test
    public void testFutures(){
        long startTime = System.currentTimeMillis();
        for (int i =0; i< th; i++){
            gobrsService.testGobrs();
            executorService.execute(()->{
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
