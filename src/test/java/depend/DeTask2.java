package depend;


import com.gobrs.platform.async.callback.ICallback;
import com.gobrs.platform.async.callback.ITask;
import com.gobrs.platform.async.worker.TaskResult;
import com.gobrs.platform.async.wrapper.TaskWrapper;

import java.util.Map;

/**
 * @author sizegang wrote on 2019-11-20.
 */
public class DeTask2 implements ITask<TaskResult<User>, String>, ICallback<TaskResult<User>, String> {

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

}
