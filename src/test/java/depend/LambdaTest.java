package depend;

import java.util.Map;

import com.jd.platform.gobrs.async.executor.Async;
import com.jd.platform.gobrs.async.worker.TaskResult;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

/**
 * @author sjsdfg
 * @since 2020/6/14
 */
public class LambdaTest {
    public static void main(String[] args) throws Exception {
        TaskWrapper<TaskResult<User>, String> workerWrapper2 = new TaskWrapper.Builder<TaskResult<User>, String>()
                .worker((TaskResult<User> result, Map<String, TaskWrapper> allWrappers) -> {
                    System.out.println("par2的入参来自于par1： " + result.getResult());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return result.getResult().getName();
                })
                .callback((boolean success, TaskResult<User> param, TaskResult<String> workResult) ->
                        System.out.println(String.format("thread is %s, param is %s, result is %s", Thread.currentThread().getName(), param, workResult)))
                .id("third")
                .build();

        TaskWrapper<TaskResult<User>, User> workerWrapper1 = new TaskWrapper.Builder<TaskResult<User>, User>()
                .worker((TaskResult<User> result, Map<String, TaskWrapper> allWrappers) -> {
                    System.out.println("par1的入参来自于par0： " + result.getResult());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return new User("user1");
                })
                .callback((boolean success, TaskResult<User> param, TaskResult<User> workResult) ->
                        System.out.println(String.format("thread is %s, param is %s, result is %s", Thread.currentThread().getName(), param, workResult)))
                .id("second")
                .next(workerWrapper2)
                .build();

        TaskWrapper<String, User> workerWrapper = new TaskWrapper.Builder<String, User>()
                .worker((String object, Map<String, TaskWrapper> allWrappers) -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return new User("user0");
                })
                .param("0")
                .id("first")
                .next(workerWrapper1, true)
                .callback((boolean success, String param, TaskResult<User> workResult) ->
                        System.out.println(String.format("thread is %s, param is %s, result is %s", Thread.currentThread().getName(), param, workResult)))
                .build();

        //虽然尚未执行，但是也可以先取得结果的引用，作为下一个任务的入参。V1.2前写法，需要手工给
        //V1.3后，不用给wrapper setParam了，直接在worker的action里自行根据id获取即可.参考dependnew包下代码
        TaskResult<User> result = workerWrapper.getWorkResult();
        TaskResult<User> result1 = workerWrapper1.getWorkResult();
        workerWrapper1.setParam(result);
        workerWrapper2.setParam(result1);

        Async.beginPlan(3500, workerWrapper);

        System.out.println(workerWrapper2.getWorkResult());
        Async.shutDown();
    }
}
