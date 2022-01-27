package dependnew;


import io.github.memorydoc.callback.ICallback;
import io.github.memorydoc.callback.ITask;
import io.github.memorydoc.task.AsyncTask;
import io.github.memorydoc.task.TaskResult;
import io.github.memorydoc.wrapper.TaskWrapper;

import java.util.Map;

/**
 * @author sizegang wrote on 2019-11-20.
 */
public class DeTask2 implements AsyncTask<User,String> {

    @Override
    public String doTask(User object, Map<String, TaskWrapper> allWrappers) {
        System.out.println("-----------------");
        System.out.println("par1的执行结果是： " + allWrappers.get("second").getWorkResult());
        System.out.println("取par1的结果作为自己的入参，并将par1的结果加上一些东西");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user1 = (User) allWrappers.get("second").getWorkResult().getResult();
        return user1.getName() + " worker2 add";
    }

    @Override
    public String defaultValue() {
        return "default";
    }

    @Override
    public boolean nessary(User user) {
        return true;
    }

    @Override
    public void begin() {
        //System.out.println(Thread.currentThread().getName() + "- start --" + System.currentTimeMillis());
    }

    @Override
    public void result(boolean success, User param, TaskResult<String> workResult) {
        System.out.println("worker2 的结果是：" + workResult.getResult());
    }

}
