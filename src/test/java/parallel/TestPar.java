package parallel;


import com.jd.platform.async.executor.Async;
import com.jd.platform.async.executor.timer.SystemClock;
import com.jd.platform.async.wrapper.WorkerWrapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

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
//        testMultiReverse();
//        testMultiError2();
//        testMulti3();
//        testMulti3Reverse();
//        testMulti4();
//        testMulti4Reverse();
//        testMulti5();
//        testMulti5Reverse();
//        testMulti6();
//        testMulti7();
//        testMulti8();
//        testMulti9();
        testMulti9Reverse();
    }

    /**
     * 3个并行，测试不同时间的超时
     */
    private static void testNormal() throws InterruptedException, ExecutionException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .build();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .build();

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(1500, workerWrapper, workerWrapper1, workerWrapper2);
//        Async.beginWork(800, workerWrapper, workerWrapper1, workerWrapper2);
//        Async.beginWork(1000, workerWrapper, workerWrapper1, workerWrapper2);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));
        System.out.println(Async.getThreadCount());

        System.out.println(workerWrapper.getWorkResult());
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

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .build();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .next(workerWrapper1)
                .build();

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(2500, workerWrapper, workerWrapper2);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        Async.shutDown();
    }

    /**
     * 0,2同时开启,1在0后面
     * 0---1
     * 2
     */
    private static void testMultiReverse() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .depend(workerWrapper)
                .build();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .build();


        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(2500, workerWrapper, workerWrapper2);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        Async.shutDown();
    }


    /**
     * 0,2同时开启,1在0后面. 组超时,则0和2成功,1失败
     * 0---1
     * 2
     */
    private static void testMultiError() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .build();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .next(workerWrapper1)
                .build();

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

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("3")
                .build();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .next(workerWrapper3)
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .next(workerWrapper3)
                .build();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .next(workerWrapper1, workerWrapper2)
                .build();


        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(3100, workerWrapper);
//        Async.beginWork(2100, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(Async.getThreadCount());
        Async.shutDown();
    }

    /**
     * 0执行完,同时1和2, 1\2都完成后3
     *     1
     * 0       3
     *     2
     */
    private static void testMulti3Reverse() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();
        ParWorker3 w3 = new ParWorker3();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .build();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .depend(workerWrapper)
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .depend(workerWrapper)
                .build();

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("3")
                .depend(workerWrapper1, workerWrapper2)
                .build();


        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(3100, workerWrapper);
//        Async.beginWork(2100, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(Async.getThreadCount());
        Async.shutDown();
    }


    /**
     * 0执行完,同时1和2, 1\2都完成后3，2耗时2秒，1耗时1秒。3会等待2完成
     *     1
     * 0       3
     *     2
     *
     * 执行结果0，1，2，3
     */
    private static void testMulti4() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();

        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(2000);

        ParWorker3 w3 = new ParWorker3();

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("3")
                .build();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .next(workerWrapper3)
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .next(workerWrapper3)
                .build();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .next(workerWrapper1, workerWrapper2)
                .build();

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

        System.out.println(Async.getThreadCount());
        Async.shutDown();
    }

    /**
     * 0执行完,同时1和2, 1\2都完成后3，2耗时2秒，1耗时1秒。3会等待2完成
     *     1
     * 0       3
     *     2
     *
     * 执行结果0，1，2，3
     */
    private static void testMulti4Reverse() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();

        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(2000);

        ParWorker3 w3 = new ParWorker3();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .build();

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("3")
                .build();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .depend(workerWrapper)
                .next(workerWrapper3)
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .depend(workerWrapper)
                .next(workerWrapper3)
                .build();

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

        System.out.println(Async.getThreadCount());
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

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("3")
                .build();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .next(workerWrapper3, false)
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .next(workerWrapper3, false)
                .build();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .next(workerWrapper1, workerWrapper2)
                .build();

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        //正常完毕
        Async.beginWork(4100, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(Async.getThreadCount());
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
    private static void testMulti5Reverse() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();

        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(500);

        ParWorker3 w3 = new ParWorker3();
        w3.setSleepTime(400);

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .build();

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("3")
                .build();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .depend(workerWrapper, true)
                .next(workerWrapper3, false)
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .depend(workerWrapper, true)
                .next(workerWrapper3, false)
                .build();



        long now = SystemClock.now();
        System.out.println("begin-" + now);

        //正常完毕
        Async.beginWork(4100, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(Async.getThreadCount());
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
     *
     * 2，3分别是500、400.2执行完了，1没完，那就等着1完毕，才能3
     */
    private static void testMulti6() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();

        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(500);

        ParWorker3 w3 = new ParWorker3();
        w3.setSleepTime(400);

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("3")
                .build();

        //设置2不是必须
        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .next(workerWrapper3, false)
                .build();
        // 设置1是必须的
        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .next(workerWrapper3, true)
                .build();

        WorkerWrapper<String, String> workerWrapper0 =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .next(workerWrapper2, workerWrapper1)
                .build();


        long now = SystemClock.now();
        System.out.println("begin-" + now);

        //正常完毕
        Async.beginWork(4100, workerWrapper0);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(Async.getThreadCount());
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

        WorkerWrapper<String, String> workerWrapper4 =  new WorkerWrapper.Builder<String, String>()
                .worker(w4)
                .callback(w4)
                .param("4")
                .build();

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("3")
                .next(workerWrapper4)
                .build();

        //下面的2
        WorkerWrapper<String, String> workerWrapper22 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("22")
                .next(workerWrapper4)
                .build();

        //下面的1
        WorkerWrapper<String, String> workerWrapper11 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("11")
                .next(workerWrapper22)
                .build();

        //下面的0
        WorkerWrapper<String, String> workerWrapper00 =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("00")
                .next(workerWrapper11)
                .build();

        //上面的1
        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .next(workerWrapper3)
                .build();

        //上面的2
        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .next(workerWrapper3)
                .build();

        //上面的0
        WorkerWrapper<String, String> workerWrapper0 =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .next(workerWrapper1, workerWrapper2)
                .build();

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        //正常完毕
        Async.beginWork(4100, workerWrapper00, workerWrapper0);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(Async.getThreadCount());
        Async.shutDown();
    }

    /**
     * a1 -> b -> c
     * a2 -> b -> c
     *
     * b、c
     */
    private static void testMulti8() throws ExecutionException, InterruptedException {
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        w1.setSleepTime(1005);

        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(3000);
        ParWorker3 w3 = new ParWorker3();
        w3.setSleepTime(1000);

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("c")
                .build();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("b")
                .next(workerWrapper3)
                .build();

        WorkerWrapper<String, String> workerWrappera1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("a1")
                .next(workerWrapper2)
                .build();
        WorkerWrapper<String, String> workerWrappera2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("a2")
                .next(workerWrapper2)
                .build();


        Async.beginWork(6000, workerWrappera1, workerWrappera2);
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

        WorkerWrapper<String, String> last =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("last")
                .build();

        WorkerWrapper<String, String> wrapperW =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("w")
                .next(last, false)
                .build();

        WorkerWrapper<String, String> wrapperW3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("w3")
                .next(last, false)
                .build();

        WorkerWrapper<String, String> wrapperW2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("w2")
                .next(wrapperW3)
                .build();

        WorkerWrapper<String, String> wrapperW1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("w1")
                .next(wrapperW2)
                .build();

        Async.beginWork(6000, wrapperW, wrapperW1);
        Async.shutDown();
    }

    /**
     * w1 -> w2 -> w3
     *            ---  last
     * w
     * w1和w并行，w执行完后就执行last，此时b、c还没开始，b、c就不需要执行了
     */
    private static void testMulti9Reverse() throws ExecutionException, InterruptedException {
        ParWorker1 w1 = new ParWorker1();
        //注意这里，如果w1的执行时间比w长，那么w2和w3肯定不走。 如果w1和w执行时间一样长，多运行几次，会发现w2有时走有时不走
//        w1.setSleepTime(1100);

        ParWorker w = new ParWorker();
        ParWorker2 w2 = new ParWorker2();
        ParWorker3 w3 = new ParWorker3();
        ParWorker4 w4 = new ParWorker4();

        WorkerWrapper<String, String> wrapperW1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("w1")
                .build();

        WorkerWrapper<String, String> wrapperW =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("w")
                .build();

        WorkerWrapper<String, String> last =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("last")
                .depend(wrapperW)
                .build();

        WorkerWrapper<String, String> wrapperW2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("w2")
                .depend(wrapperW1)
                .build();

        WorkerWrapper<String, String> wrapperW3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("w3")
                .depend(wrapperW2)
                .next(last, false)
                .build();

        Async.beginWork(6000,Executors.newCachedThreadPool(),  wrapperW, wrapperW1);
        Async.shutDown();
    }
}
