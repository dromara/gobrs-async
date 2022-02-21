package com.jd.gobrs.async.example.service;

import com.jd.gobrs.async.gobrs.GobrsTaskFlow;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @program: gobrs-async-example
 * @ClassName GobrsService
 * @description:
 * @author: sizegang
 * @create: 2022-01-29 21:00
 * @Version 1.0
 **/
@Service
public class GobrsService {

    @Resource
    private GobrsTaskFlow taskFlow;


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
    @Resource
    List<AsyncTask> asyncTasks;

    @Autowired
    private ThreadPoolTaskExecutor gobrsThreadPoolExecutor;


    public void testGobrs() {
        try {
            taskFlow.taskFlow("test", () -> {
                Map<String, Object> params = new HashMap<>();
                params.put("AService", "AService param");
                return params;
            }, 100000);
        } catch (Exception ex) {
            System.out.println("异常了" + ex);
        }
    }

    public void testGobrs2() {
        List<Future> list = new ArrayList<>();
        for (AsyncTask asyncTask : asyncTasks) {
            Future<?> submit = gobrsThreadPoolExecutor.submit(() -> {
//                asyncTask.doTask("", new TaskResult());
                long cost = System.currentTimeMillis();
            });
            list.add(submit);
        }
        for (Future future : list) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void testGobrs3() {
        List<Future> list = new ArrayList<>();

        for (AsyncTask asyncTask : asyncTasks) {
            CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
//                asyncTask.doTask("", new HashMap<>());
            }, gobrsThreadPoolExecutor);
            list.add(runAsync);
        }
        CompletableFuture[] strings = new CompletableFuture[list.size()];
        CompletableFuture.allOf(list.toArray(strings));
    }
}
