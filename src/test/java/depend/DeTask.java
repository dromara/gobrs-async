package depend;


import com.gobrs.platform.async.callback.ICallback;
import com.gobrs.platform.async.callback.ITask;
import com.gobrs.platform.async.worker.TaskResult;
import com.gobrs.platform.async.wrapper.TaskWrapper;

import java.util.Map;

/**
 * @author sizegang wrote on 2019-11-20.
 */
public class DeTask implements ITask<String, User>, ICallback<String, User> {

    @Override
    public User doTask(String object, Map<String, TaskWrapper> allWrappers) {
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
    public void result(boolean success, String param, TaskResult<User> workResult) {
        System.out.println("worker0 的结果是：" + workResult.getResult());
    }

}
