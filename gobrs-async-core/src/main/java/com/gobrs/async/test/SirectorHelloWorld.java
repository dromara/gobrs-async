package com.gobrs.async.test;

import com.gobrs.async.domain.AsyncParam;
import com.gobrs.async.task.AsyncTask;
import com.gobrs.async.Callback;
import com.gobrs.async.GobrsAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: gobrs-async-starter
 * @ClassName SirectorHelloWorld
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/


@SuppressWarnings("unchecked")
public class SirectorHelloWorld {

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {

        GobrsAsync gobrsAsync = new GobrsAsync(executorService);

        //准备事件处理器实例和回调实例
        AService aService = new AService();
        BService bService = new BService();
        CService cService = new CService();
        DService dService = new DService();
        EService eService = new EService();

        Callback alertCallback = new AlertCallback();

        //编排事件处理器
        gobrsAsync.begin(aService, bService).then(cService).then(eService);
        gobrsAsync.after(eService).then(dService);
        gobrsAsync.readyTo();

        //同步发布事件
        gobrsAsync.go(() -> new Object());
        //异步发布事件
//        gobrsAsync.start(() -> new Object(), alertCallback);
    }

    static class HelloWorldEventHandler implements
            AsyncTask<Object, Object> {

        private final int times;

        public HelloWorldEventHandler(int times) {
            this.times = times;
        }


        @Override
        public Object task(Object o) {
//            if(true){
//                while (true){
//                    System.out.println(111);
//                }
//            }
            System.out.println(times);
            return null;
        }

        @Override
        public boolean nessary(Object o) {
            return true;
        }
    }

    static class AlertCallback implements Callback {

        @Override
        public void onError(AsyncParam event, Throwable throwable) {
            //处理异常
        }

        @Override
        public void onSuccess(AsyncParam event) {

        }

    }

}
