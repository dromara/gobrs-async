package parallel;


import dependnew.User;
import io.github.memorydoc.callback.ICallback;
import io.github.memorydoc.callback.ITask;
import io.github.memorydoc.executor.timer.SystemClock;
import io.github.memorydoc.task.AsyncTask;
import io.github.memorydoc.task.TaskResult;
import io.github.memorydoc.wrapper.TaskWrapper;

import java.util.Map;

/**
 * @author sizegang wrote on 2019-11-20.
 */
public class ParTimeoutTask implements AsyncTask<String,String> {

    @Override
    public String doTask(String object, Map<String, TaskWrapper> allWrappers) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "result = " + SystemClock.now() + "---param = " + object + " from 0";
    }


    @Override
    public String defaultValue() {
        return "worker0--default";
    }


    @Override
    public void begin() {
        //System.out.println(Thread.currentThread().getName() + "- start --" + System.currentTimeMillis());
    }
    @Override
    public boolean nessary(String s) {
        return true;
    }
    @Override
    public void result(boolean success, String param, TaskResult<String> workResult) {
        if (success) {
            System.out.println("callback worker0 success--" + SystemClock.now() + "----" + workResult.getResult()
                    + "-threadName:" +Thread.currentThread().getName());
        } else {
            System.err.println("callback worker0 failure--" + SystemClock.now() + "----"  + workResult.getResult()
                    + "-threadName:" +Thread.currentThread().getName());
        }
    }

}
