package com.gobrs.async.test;

import com.gobrs.async.EventHandler;
import com.gobrs.async.Sirector;
import com.gobrs.async.TimeoutException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: gobrs-async-starter
 * @ClassName SirectorSynTimeout
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/

@SuppressWarnings("unchecked")
public class SirectorSynTimeout {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Sirector<Event> sirector = new Sirector<SirectorSynTimeout.Event>(executorService);
        SleepHandler handler = new SleepHandler();
        sirector.begin(handler);
        sirector.ready();
        try{
            sirector.publish(new Event(), 1000/*timeout in millisecond*/);
        }catch (TimeoutException e) {
            /*handle timeout exception*/
            e.printStackTrace();
        }

    }

    static class Event{

    }

    static class SleepHandler implements EventHandler<Event> {

        @Override
        public void onEvent(Event event) {
            try{
                Thread.sleep(10000);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}