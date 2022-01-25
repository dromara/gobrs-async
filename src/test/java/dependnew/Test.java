package dependnew;

import com.jd.platform.gobrs.async.executor.Async;
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
        TaskWrapper<User, String> workerWrapper2 =  new TaskWrapper.Builder<User, String>()
                .worker(w2)
                .callback(w2)
                .id("third")
                .build();

        TaskWrapper<String, User> workerWrapper1 = new TaskWrapper.Builder<String, User>()
                .worker(w1)
                .callback(w1)
                .id("second")
                .next(workerWrapper2)
                .build();

        TaskWrapper<String, User> workerWrapper = new TaskWrapper.Builder<String, User>()
                .worker(w)
                .callback(w)
                .param("0")
                .id("first")
                .next(workerWrapper1)
                .build();

        //V1.3后，不用给wrapper setParam了，直接在worker的action里自行根据id获取即可

        Async.beginTaskFlow(3500, workerWrapper);

        System.out.println(workerWrapper2.getWorkResult());
        Async.shutDown();
    }
}
