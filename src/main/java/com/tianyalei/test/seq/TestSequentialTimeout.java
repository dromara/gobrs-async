package com.tianyalei.test.seq;


import com.tianyalei.async.executor.Async;
import com.tianyalei.async.executor.timer.SystemClock;
import com.tianyalei.async.wrapper.WorkerWrapper;

import java.util.concurrent.ExecutionException;

/**
 * 串行测试
 * @author wuweifeng wrote on 2019-11-20.
 */
@SuppressWarnings("Duplicates")
public class TestSequentialTimeout {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
//        testFirstTimeout();

        testSecondTimeout();
    }

    /**
     * begin-1576719450476
     * callback worker0 failure--1576719451338----worker0--default-threadName:main
     * callback worker1 failure--1576719451338----worker1--default-threadName:main
     * callback worker2 failure--1576719451338----worker2--default-threadName:main
     * end-1576719451338
     * cost-862
     */
    private static void testFirstTimeout() throws ExecutionException, InterruptedException {
        SeqWorker1 w1 = new SeqWorker1();
        SeqWorker2 w2 = new SeqWorker2();
        SeqTimeoutWorker t = new SeqTimeoutWorker();

        WorkerWrapper<String, String> workerWrapperT = new WorkerWrapper<>(t, "t", t);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);

        //2在1后面串行
        workerWrapper1.addNext(workerWrapper2);
        //T会超时
        workerWrapperT.addNext(workerWrapper1);
        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(5000, workerWrapperT);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        Async.shutDown();
    }

    /**
     * begin-1576719842504
     * callback worker0 success--1576719843571----result = 1576719843570---param = t from 0-threadName:Thread-0
     * callback worker1 failure--1576719844376----worker1--default-threadName:main
     * callback worker2 failure--1576719844376----worker2--default-threadName:main
     * end-1576719844376
     * cost-1872
     */
    private static void testSecondTimeout() throws ExecutionException, InterruptedException {
        SeqTimeoutWorker t = new SeqTimeoutWorker();

        //让1超时
        SeqWorker1 w1 = new SeqWorker1();

        SeqWorker2 w2 = new SeqWorker2();

        WorkerWrapper<String, String> workerWrapperT = new WorkerWrapper<>(t, "t", t);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);

        //2在1后面串行
        workerWrapper1.addNext(workerWrapper2);
        //T会超时
        workerWrapperT.addNext(workerWrapper1);
        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(5000, workerWrapperT);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        Async.shutDown();
    }
}
