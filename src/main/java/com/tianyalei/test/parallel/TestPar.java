package com.tianyalei.test.parallel;


import com.tianyalei.async.executor.Async;
import com.tianyalei.async.executor.timer.SystemClock;
import com.tianyalei.async.wrapper.WorkerWrapper;

import java.util.concurrent.ExecutionException;

import static com.tianyalei.async.executor.Async.getThreadCount;

/**
 * 并行测试
 *
 * @author wuweifeng wrote on 2019-11-20.
 */
@SuppressWarnings("ALL")
public class TestPar {
    public static void main(String[] args) throws Exception {


//        testNormal();
//        testMulti();
//        testMultiError();
//        testMultiError2();
//        testMulti3();
//        testMulti4();
//        testMulti5();
//        testMulti6();
//        testMulti7();
//        testMulti8();
        testMulti9();
    }

    /**
     * 3个并行，测试不同时间的超时
     */
    private static void testNormal() throws InterruptedException, ExecutionException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(1500, workerWrapper, workerWrapper1, workerWrapper2);
//        Async.beginWork(800, workerWrapper, workerWrapper1, workerWrapper2);
//        Async.beginWork(1000, workerWrapper, workerWrapper1, workerWrapper2);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));
        System.out.println(getThreadCount());

        System.out.println(workerWrapper.getWorkResult());
//        System.out.println(getThreadCount());
        Async.shutDown();
    }

    /**
     * 0,2同时开启,1在0后面
     * 0---1
     * 2
     */
    private static void testMulti() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);

        workerWrapper.addNext(workerWrapper1);

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(2500, workerWrapper, workerWrapper2);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        Async.shutDown();
    }

    /**
     * 0,2同时开启,1在0后面. 1超时
     * 0---1
     * 2
     */
    private static void testMultiError() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);

        workerWrapper.addNext(workerWrapper1);

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(1500, workerWrapper, workerWrapper2);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        Async.shutDown();
    }

    /**
     * 0,2同时开启,1在0后面. 组超时,则0和2成功,1失败
     * 0---1
     * 2
     */
    private static void testMultiError2() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);

        workerWrapper.addNext(workerWrapper1);

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(1500, workerWrapper, workerWrapper2);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        Async.shutDown();
    }

    /**
     * 0执行完,同时1和2, 1\2都完成后3
     *     1
     * 0       3
     *     2
     */
    private static void testMulti3() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();
        ParWorker3 w3 = new ParWorker3();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "3", w3);

        workerWrapper.addNext(workerWrapper1, workerWrapper2);
        workerWrapper1.addNext(workerWrapper3);
        workerWrapper2.addNext(workerWrapper3);

        long now = SystemClock.now();
        System.out.println("begin-" + now);

//        Async.beginWork(3100, workerWrapper);
        Async.beginWork(2100, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(getThreadCount());
        Async.shutDown();
    }

    /**
     * 0执行完,同时1和2, 1\2都完成后3，2耗时2秒，1耗时1秒。3会等待2完成
     *     1
     * 0       3
     *     2
     */
    private static void testMulti4() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(2000);
        ParWorker3 w3 = new ParWorker3();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "3", w3);

        workerWrapper.addNext(workerWrapper1, workerWrapper2);
        workerWrapper1.addNext(workerWrapper3);
        workerWrapper2.addNext(workerWrapper3);

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        //正常完毕
        Async.beginWork(4100, workerWrapper);
        //3会超时
//        Async.beginWork(3100, workerWrapper);
        //2,3会超时
//        Async.beginWork(2900, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(getThreadCount());
        Async.shutDown();
    }

    /**
     * 0执行完,同时1和2, 1\2 任何一个执行完后，都执行3
     *     1
     * 0       3
     *     2
     *
     * 则结果是：
     * 0，2，3，1
     * 2，3分别是500、400.3执行完毕后，1才执行完
     */
    private static void testMulti5() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(500);
        ParWorker3 w3 = new ParWorker3();
        w3.setSleepTime(400);

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "3", w3);

        workerWrapper.addNext(workerWrapper1, workerWrapper2);
        workerWrapper1.addNext(workerWrapper3);
        workerWrapper2.addNext(workerWrapper3);
        workerWrapper3.setDependNotMust(workerWrapper1, workerWrapper2);

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        //正常完毕
        Async.beginWork(4100, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(getThreadCount());
        Async.shutDown();
    }

    /**
     * 0执行完,同时1和2, 必须1执行完毕后，才能执行3. 无论2是否领先1完毕，都要等1
     *     1
     * 0       3
     *     2
     *
     * 则结果是：
     * 0，2，1，3
     * 2，3分别是500、400.2执行完了，1没完，那就等着1完毕，才能3
     */
    private static void testMulti6() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(500);
        ParWorker3 w3 = new ParWorker3();
        w3.setSleepTime(400);

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "3", w3);

        workerWrapper.addNext(workerWrapper1, workerWrapper2);
        workerWrapper1.addNext(workerWrapper3);
        workerWrapper2.addNext(workerWrapper3);
        //设置2不是必须，1是必须的
        workerWrapper3.setDependNotMust(workerWrapper2);

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        //正常完毕
        Async.beginWork(4100, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(getThreadCount());
        Async.shutDown();
    }

    /**
     * 两个0并行，上面0执行完,同时1和2, 下面0执行完开始1，上面的 必须1、2执行完毕后，才能执行3. 最后必须2、3都完成，才能4
     *     1
     * 0       3
     *     2        4
     * ---------
     * 0   1   2
     *
     * 则结果是：
     * callback worker0 success--1577242870969----result = 1577242870968---param = 00 from 0-threadName:Thread-1
     * callback worker0 success--1577242870969----result = 1577242870968---param = 0 from 0-threadName:Thread-0
     * callback worker1 success--1577242871972----result = 1577242871972---param = 11 from 1-threadName:Thread-1
     * callback worker1 success--1577242871972----result = 1577242871972---param = 1 from 1-threadName:Thread-2
     * callback worker2 success--1577242871973----result = 1577242871973---param = 2 from 2-threadName:Thread-3
     * callback worker2 success--1577242872975----result = 1577242872975---param = 22 from 2-threadName:Thread-1
     * callback worker3 success--1577242872977----result = 1577242872977---param = 3 from 3-threadName:Thread-2
     * callback worker4 success--1577242873980----result = 1577242873980---param = 4 from 3-threadName:Thread-2
     */
    private static void testMulti7() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();
        ParWorker3 w3 = new ParWorker3();
        ParWorker4 w4 = new ParWorker4();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper0 = new WorkerWrapper<>(w, "00", w);

        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper11 = new WorkerWrapper<>(w1, "11", w1);

        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        WorkerWrapper<String, String> workerWrapper22 = new WorkerWrapper<>(w2, "22", w2);

        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "3", w3);
        WorkerWrapper<String, String> workerWrapper4 = new WorkerWrapper<>(w3, "4", w4);

        workerWrapper.addNext(workerWrapper1, workerWrapper2);
        workerWrapper1.addNext(workerWrapper3);
        workerWrapper2.addNext(workerWrapper3);
        workerWrapper3.addNext(workerWrapper4);

        workerWrapper0.addNext(workerWrapper11);
        workerWrapper11.addNext(workerWrapper22);
        workerWrapper22.addNext(workerWrapper4);

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        //正常完毕
        Async.beginWork(4100, workerWrapper, workerWrapper0);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(getThreadCount());
        Async.shutDown();
    }

    /**
     * a1 -> b -> c
     * a2 -> b -> c
     */
    private static void testMulti8() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
//        w1.setSleepTime(1005);

        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(3000);
        ParWorker3 w3 = new ParWorker3();
        w3.setSleepTime(1000);

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "a1", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "a2", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "b", w2);
        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "c", w3);
        workerWrapper.addNext(workerWrapper2);
        workerWrapper1.addNext(workerWrapper2);

        workerWrapper2.addNext(workerWrapper3);

        Async.beginWork(6000, workerWrapper, workerWrapper1);
        Async.shutDown();
    }

    /**
     * w1 -> w2 -> w3
     *            ---  last
     * w
     * w1和w并行，w执行完后就执行last，此时b、c还没开始，b、c就不需要执行了
     */
    private static void testMulti9() throws ExecutionException, InterruptedException {
        ParWorker1 w1 = new ParWorker1();
        //注意这里，如果w1的执行时间比w长，那么w2和w3肯定不走。 如果w1和w执行时间一样长，多运行几次，会发现w2有时走有时不走
//        w1.setSleepTime(1100);

        ParWorker w = new ParWorker();
        ParWorker2 w2 = new ParWorker2();
        ParWorker3 w3 = new ParWorker3();
        ParWorker4 w4 = new ParWorker4();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "w", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "w1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "w2", w2);
        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "w3", w3);
        WorkerWrapper<String, String> last = new WorkerWrapper<>(w3, "last", w4);

        workerWrapper1.addNext(workerWrapper2);
        workerWrapper2.addNext(workerWrapper3);
        workerWrapper3.addNext(last);

        workerWrapper.addNext(last);

        last.setDependNotMust(workerWrapper);
        last.setDependNotMust(workerWrapper3);

        Async.beginWork(6000, workerWrapper, workerWrapper1);
        Async.shutDown();
    }
}
