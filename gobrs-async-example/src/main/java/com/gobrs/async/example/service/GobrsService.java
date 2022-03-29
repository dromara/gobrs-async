package com.gobrs.async.example.service;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.example.task.*;
import com.gobrs.async.task.AsyncTask;
import com.jd.platform.async.executor.Async;
import com.jd.platform.async.wrapper.WorkerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @program: gobrs-async-core
 * @ClassName GobrsService
 * @description:
 * @author: sizegang
 * @create: 2022-03-28
 **/
@Service
public class GobrsService {

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

    @Autowired
    private GobrsAsync gobrsAsync;

    ExecutorService executorService = Executors.newCachedThreadPool();


    public void gobrsAsync() {
        gobrsAsync.go("test", () -> new Object());
    }


    public void asyncTool() {

        WorkerWrapper<String, String> gWorker = new WorkerWrapper.Builder<String, String>()
                .worker(gService)
                .build();

        WorkerWrapper<String, String> eWorker = new WorkerWrapper.Builder<String, String>()
                .worker(eService)
                .next(gWorker)
                .build();
        WorkerWrapper<String, String> fWorker = new WorkerWrapper.Builder<String, String>()
                .worker(fService)
                .next(gWorker)
                .build();


        WorkerWrapper<String, String> cWorker = new WorkerWrapper.Builder<String, String>()
                .worker(cService)
                .next(eWorker)
                .build();
        WorkerWrapper<String, String> dWorker = new WorkerWrapper.Builder<String, String>()
                .worker(dService)
                .next(fWorker)
                .build();


        WorkerWrapper<String, String> aWorker = new WorkerWrapper.Builder<String, String>()
                .worker(bService)
                .next(dWorker)
                .build();
        WorkerWrapper<String, String> bWorker = new WorkerWrapper.Builder<String, String>()
                .worker(aService)
                .next(cWorker)
                .build();
        try {
            Async.beginWork(10000, aWorker, bWorker);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


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
