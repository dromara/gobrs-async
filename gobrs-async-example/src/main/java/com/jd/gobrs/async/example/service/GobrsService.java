package com.jd.gobrs.async.example.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.gobrs.async.example.executor.ParaExector;
import com.jd.gobrs.async.example.executor.SerExector;
import com.jd.gobrs.async.gobrs.GobrsTaskFlow;
import com.jd.gobrs.async.result.AsyncResult;
import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import com.jd.gobrs.async.wrapper.TaskWrapper;
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

    @Resource
    List<ParaExector> paraExectors;

    @Resource
    List<SerExector> serExectors;

    @Autowired
    private ThreadPoolTaskExecutor gobrsThreadPoolExecutor;


    public void testGobrs() {
        AsyncResult<Object> asyncResult = null;
        try {
            asyncResult = taskFlow.taskFlow("test", () -> {
                Map<String, Object> params = new HashMap<>();
                params.put("AService", "AService param");
                return params;
            }, 5000);
        } catch (Exception e) {
            System.out.println("异常 " + e);
        }
        System.out.println(asyncResult.getExpCode());
        Map<String, Object> data = asyncResult.getData();
        System.out.println(JSONObject.toJSONString(data));
    }

    public void testFuture() {
        List<Future> list = new ArrayList<>();
        for (AsyncTask asyncTask : paraExectors) {
            Future<?> submit = gobrsThreadPoolExecutor.submit(() -> {
                asyncTask.task("", null, 0L);
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

        List<Future> ser = new ArrayList<>();
        for (AsyncTask asyncTask : serExectors) {
            Future<?> submit = gobrsThreadPoolExecutor.submit(() -> {
                asyncTask.task("", null, 0L);
            });
            ser.add(submit);
        }
        for (Future future : ser) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void testGobrsGener() {
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
