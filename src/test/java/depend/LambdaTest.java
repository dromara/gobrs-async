package depend;

import java.util.Map;

import com.jd.platform.async.executor.Async;
import com.jd.platform.async.worker.WorkResult;
import com.jd.platform.async.wrapper.WorkerWrapper;

/**
 * @author sjsdfg
 * @since 2020/6/14
 */
public class LambdaTest {
    public static void main(String[] args) throws Exception {
        WorkerWrapper<WorkResult<User>, String> workerWrapper2 = new WorkerWrapper.Builder<WorkResult<User>, String>()
                .worker((WorkResult<User> result, Map<String, WorkerWrapper> allWrappers) -> {
                    System.out.println("par2的入参来自于par1： " + result.getResult());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return result.getResult().getName();
                })
                .callback((boolean success, WorkResult<User> param, WorkResult<String> workResult) ->
                        System.out.println(String.format("thread is %s, param is %s, result is %s", Thread.currentThread().getName(), param, workResult)))
                .id("third")
                .build();

        WorkerWrapper<WorkResult<User>, User> workerWrapper1 = new WorkerWrapper.Builder<WorkResult<User>, User>()
                .worker((WorkResult<User> result, Map<String, WorkerWrapper> allWrappers) -> {
                    System.out.println("par1的入参来自于par0： " + result.getResult());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return new User("user1");
                })
                .callback((boolean success, WorkResult<User> param, WorkResult<User> workResult) ->
                        System.out.println(String.format("thread is %s, param is %s, result is %s", Thread.currentThread().getName(), param, workResult)))
                .id("second")
                .next(workerWrapper2)
                .build();

        WorkerWrapper<String, User> workerWrapper = new WorkerWrapper.Builder<String, User>()
                .worker((String object, Map<String, WorkerWrapper> allWrappers) -> {
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
                .callback((boolean success, String param, WorkResult<User> workResult) ->
                        System.out.println(String.format("thread is %s, param is %s, result is %s", Thread.currentThread().getName(), param, workResult)))
                .build();

        //虽然尚未执行，但是也可以先取得结果的引用，作为下一个任务的入参。V1.2前写法，需要手工给
        //V1.3后，不用给wrapper setParam了，直接在worker的action里自行根据id获取即可.参考dependnew包下代码
        WorkResult<User> result = workerWrapper.getWorkResult();
        WorkResult<User> result1 = workerWrapper1.getWorkResult();
        workerWrapper1.setParam(result);
        workerWrapper2.setParam(result1);

        Async.beginWork(3500, workerWrapper);

        System.out.println(workerWrapper2.getWorkResult());
        Async.shutDown();
    }
}
