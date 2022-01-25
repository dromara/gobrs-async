package depend;


import com.jd.platform.gobrs.async.callback.ICallback;
import com.jd.platform.gobrs.async.callback.IWorker;
import com.jd.platform.gobrs.async.worker.TaskResult;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import java.util.Map;

/**
 * @author sizegang wrote on 2019-11-20.
 */
public class DeWorker1 implements IWorker<TaskResult<User>, User>, ICallback<TaskResult<User>, User> {

    @Override
    public User action(TaskResult<User> result, Map<String, TaskWrapper> allWrappers) {
        System.out.println("par1的入参来自于par0： " + result.getResult());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new User("user1");
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
    public void result(boolean success, TaskResult<User> param, TaskResult<User> workResult) {
        System.out.println("worker1 的结果是：" + workResult.getResult());
    }

}
