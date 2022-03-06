package com.jd.gobrs.async.example.service;

import com.jd.gobrs.async.example.DataContext;
import com.jd.gobrs.async.example.executor.ParaExector;
import com.jd.gobrs.async.example.executor.SerExector;
import com.jd.gobrs.async.gobrs.GobrsTaskFlow;
import com.jd.gobrs.async.result.AsyncResult;
import com.jd.gobrs.async.task.AsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.*;

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


    @Autowired
    private ThreadPoolTaskExecutor gobrsThreadPoolExecutor;


    public void testGobrs(HttpServletRequest httpServletRequest) {
        DataContext dataContext = new DataContext();
        dataContext.setHttpServletRequest(httpServletRequest);
        AsyncResult asyncResult = taskFlow.taskFlow("test", dataContext, 100000);

    }


    @Resource
    List<AsyncTask> asyncTasks;

    @Resource
    List<ParaExector> paraExectors;

    @Resource
    List<SerExector> serExectors;

    public void testFuture(HttpServletRequest httpServletRequest) {
        DataContext dataContext = new DataContext();
        dataContext.setHttpServletRequest(httpServletRequest);
        List<Future> list = new ArrayList<>();
        for (AsyncTask asyncTask : paraExectors) {
            Future<?> submit = gobrsThreadPoolExecutor.submit(() -> {
                asyncTask.task(dataContext, null);
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
                asyncTask.task(dataContext, null);
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
