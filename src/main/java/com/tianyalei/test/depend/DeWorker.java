package com.tianyalei.test.depend;


import com.tianyalei.async.callback.ICallback;
import com.tianyalei.async.callback.IWorker;
import com.tianyalei.async.executor.timer.SystemClock;
import com.tianyalei.async.worker.WorkResult;

/**
 * @author wuweifeng wrote on 2019-11-20.
 */
public class DeWorker implements IWorker<String, User>, ICallback<String, User> {

    @Override
    public User action(String object) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new User("user0");
    }


    @Override
    public User defaultValue() {
        return new User("default User");
    }

    @Override
    public void begin() {
        //System.out.println(Thread.currentThread().getName() + "- start --" + System.currentTimeMillis());
    }

    @Override
    public void result(boolean success, String param, WorkResult<User> workResult) {
        System.out.println("worker0 的结果是：" + workResult.getResult());
    }

}
