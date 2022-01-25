package depend;

import com.jd.platform.gobrs.async.executor.Async;
import com.jd.platform.gobrs.async.worker.TaskResult;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;

import java.util.concurrent.ExecutionException;


/**
 * 后面请求依赖于前面请求的执行结果
 * @author sizegang wrote on 2019-12-26
 * @version 1.0
 */
public class Test {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        DeWorker w = new DeWorker();
        DeWorker1 w1 = new DeWorker1();
        DeWorker2 w2 = new DeWorker2();

        TaskWrapper<TaskResult<User>, String> workerWrapper2 =  new TaskWrapper.Builder<TaskResult<User>, String>()
                .worker(w2)
                .callback(w2)
                .id("third")
                .build();

        TaskWrapper<TaskResult<User>, User> workerWrapper1 = new TaskWrapper.Builder<TaskResult<User>, User>()
                .worker(w1)
                .callback(w1)
                .id("second")
                .next(workerWrapper2)
                .build();

        TaskWrapper<String, User> workerWrapper = new TaskWrapper.Builder<String, User>()
                .worker(w)
                .param("0")
                .id("first")
                .next(workerWrapper1, true)
                .callback(w)
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
