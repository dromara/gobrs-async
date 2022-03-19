package com.gobrs.async.test;

import com.gobrs.async.Callback;
import com.gobrs.async.EventHandler;
import com.gobrs.async.Sirector;

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
        Sirector<HelloWorldEvent> sirector = new Sirector<SirectorHelloWorld.HelloWorldEvent>(
                executorService);

        //准备事件处理器实例和回调实例
        HelloWorldEventHandler onceHandler = new HelloWorldEventHandler(1);
        HelloWorldEventHandler twiceHandler = new HelloWorldEventHandler(2);
        HelloWorldEventHandler threeTimesHandler = new HelloWorldEventHandler(3);
        HelloWorldEventHandler fourTimesHandler = new HelloWorldEventHandler(4);
        Callback<HelloWorldEvent> alertCallback = new AlertCallback();

        //编排事件处理器
        sirector.begin(onceHandler).then(twiceHandler);
        sirector.after(onceHandler).then(threeTimesHandler);
        sirector.after(twiceHandler, threeTimesHandler).then(fourTimesHandler);
        sirector.ready();

        //同步发布事件
        HelloWorldEvent event = sirector.publish(new HelloWorldEvent());
        System.out.println("hello world are called " + event.callCount
                + " times");
        //异步发布事件
        sirector.publish(new HelloWorldEvent(), alertCallback);
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
            EventHandler<HelloWorldEvent> {

        private final int times;

        public HelloWorldEventHandler(int times) {
            this.times = times;
        }

        @Override
        public void onEvent(HelloWorldEvent t) {
            for (int i = 0; i < times; i++) {
                t.increaseCallCount();
            }
        }

    }

    static class AlertCallback implements Callback<HelloWorldEvent> {

        @Override
        public void onError(HelloWorldEvent event, Throwable throwable) {
            //处理异常
        }

        @Override
        public void onSuccess(HelloWorldEvent event) {
            System.out.println("hello world are called " + event.callCount
                    + " times");
        }

    }

}
