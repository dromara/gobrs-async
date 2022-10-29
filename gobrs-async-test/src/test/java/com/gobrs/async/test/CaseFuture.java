package com.gobrs.async.test;

import com.gobrs.async.test.task.*;
import com.gobrs.async.core.task.AsyncTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The type Case future.
 *
 * @program: gobrs -async
 * @ClassName CaseFuture
 * @description:
 * @author: sizegang
 * @create: 2022 -09-28
 */
public class CaseFuture {
    /**
     * The Executor service.
     */
    ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    private AService aService;

    @Autowired
    private BService bService;
    @Autowired
    private CService cService;
    @Autowired
    private DService dService;

    @Autowired
    private EService eService;

    @Autowired
    private FService fService;

    @Autowired
    private GService gService;

    /**
     * Futures test.
     */
    @Test
    public void futuresTest() {
        long start = System.currentTimeMillis();
        future();
        long coust = System.currentTimeMillis() - start;
        System.out.println("future " + coust);
    }


    /**
     * Future.
     */
    public void future() {
        List<AsyncTask> abList = new ArrayList<>();
        abList.add(aService);
        abList.add(bService);
        List<Future> futures = new ArrayList<>();
        for (AsyncTask task : abList) {
            Future<Object> submit = executorService.submit(() -> task.task(new Object(), null));
            futures.add(submit);
        }

        for (Future future : futures) {
            try {
                Object o = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        List<AsyncTask> cdList = new ArrayList<>();
        cdList.add(cService);
        cdList.add(dService);
        List<Future> futurescd = new ArrayList<>();
        for (AsyncTask task : cdList) {
            Future<Object> submit = executorService.submit(() -> task.task(new Object(), null));
            futurescd.add(submit);
        }

        for (Future future : futurescd) {
            try {
                Object o = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        List<AsyncTask> efList = new ArrayList<>();
        efList.add(eService);
        efList.add(fService);
        List<Future> futuresef = new ArrayList<>();
        for (AsyncTask task : efList) {
            Future<Object> submit = executorService.submit(() -> task.task(new Object(), null));
            futuresef.add(submit);
        }

        for (Future future : futuresef) {
            try {
                Object o = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        Future<Object> submit = executorService.submit(() -> gService.task(new Object(), null));
        try {
            submit.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
