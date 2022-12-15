package com.gobrs.async.test.task.retry;

import com.gobrs.async.core.TaskSupport;
import com.gobrs.async.core.anno.Task;
import com.gobrs.async.core.task.AsyncTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * The type Case retry task b.
 *
 * @program: gobrs -async
 * @ClassName GobrsTaskA
 * @description:
 * @author: sizegang
 * @create: 2022 -10-31
 */
@Slf4j
@Task(timeoutInMilliseconds = 10, failSubExec = true)
public class CaseRetryTaskB extends AsyncTask {

    private static int l;

    @Override
    public void prepare(Object o) {
        log.info(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }


    @SneakyThrows
    @Override
    public Object task(Object o, TaskSupport support) {
        System.out.println("CaseRetryTaskB Begin");
        Long j = 0L;
        for (int i = 0; i < 100000000000000000L; i++) {
            if (i % 100000L == 0) {
                System.out.println("test");
            }
            j++;
        }
//        Thread.sleep(100);
        System.out.println("CaseRetryTaskB End");
        return "AResult";
    }


    public static void main(String[] args) {

        Runnable runnable = () -> {
            for (int i = 0; i < 100L; i++) {
                l++;
            }
        };
        Future<?> submit = Executors.newCachedThreadPool().submit(runnable);

        try {
            submit.get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        System.out.println("结束了");

    }


}
