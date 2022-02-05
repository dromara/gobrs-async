package depend;


import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import com.jd.gobrs.async.wrapper.TaskWrapper;

import java.util.Map;

/**
 * @author sizegang wrote on 2019-11-20.
 */
public class DeTask2 implements AsyncTask<TaskResult<User>, String> {

    @Override
    public String doTask(TaskResult<User> result, Map<String, TaskWrapper> allWrappers) {
        System.out.println("par2的入参来自于par1： " + result.getResult());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result.getResult().getName();
    }


    @Override
    public String defaultValue() {
        return "default";
    }

    @Override
    public void begin() {
        //System.out.println(Thread.currentThread().getName() + "- start --" + System.currentTimeMillis());
    }

    @Override
    public void result(boolean success, TaskResult<User> param, TaskResult<String> workResult) {
        System.out.println("worker2 的结果是：" + workResult.getResult());
    }

    @Override
    public boolean nessary(TaskResult<User> userTaskResult) {
        return false;
    }
}
