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
        HelloWorldEventHandler onceHandler = new HelloWorldEventHandler(1);
        HelloWorldEventHandler twiceHandler = new HelloWorldEventHandler(2);
        HelloWorldEventHandler threeTimesHandler = new HelloWorldEventHandler(3);
        HelloWorldEventHandler fourTimesHandler = new HelloWorldEventHandler(4);
        Callback<HelloWorldEvent> alertCallback = new AlertCallback();

        //编排事件处理器
        gobrsAsync.begin(onceHandler).then(threeTimesHandler);
        gobrsAsync.after(threeTimesHandler).then(twiceHandler);
        gobrsAsync.after(twiceHandler).then(fourTimesHandler);
        gobrsAsync.ready();

        //同步发布事件
        gobrsAsync.start(() -> new Object(), 100);
        //异步发布事件
//        gobrsAsync.start(() -> new Object(), alertCallback);
    }

    static class HelloWorldEvent {

        private int callCount;

        public void increaseCallCount() {
            callCount++;
        }

        public int getCallCount() {
            return callCount;
        }

    }

    static class HelloWorldEventHandler implements
            AsyncTask<Object, Object> {

        private final int times;

        public HelloWorldEventHandler(int times) {
            this.times = times;
        }


        @Override
        public Object task(Object o) {
            if(true){
                while (true){
                    System.out.println(111);
                }
            }
            System.out.println(times);
            return null;
        }

        @Override
        public boolean nessary(Object o) {
            return false;
        }
    }

    static class AlertCallback implements Callback<HelloWorldEvent> {

        @Override
        public void onError(AsyncParam event, Throwable throwable) {
            //处理异常
        }

        @Override
        public void onSuccess(AsyncParam event) {

        }

    }

}
