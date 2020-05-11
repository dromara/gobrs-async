如果只是需要用这个框架，请往下看即可。如果需要深入了解这个框架是如何一步一步实现的，从接到需求，到每一步的思考，每个类为什么这么设计，为什么有这些方法，也就是如何从0到1开发出这个框架，作者在[csdn开了专栏](https://blog.csdn.net/tianyaleixiaowu/category_9637010.html)专门讲中间件如何从0开发，包括并不限于这个小框架。京东内部同事可在cf上搜索erp也能看到。

京东同事通过引用如下maven来使用。

```
            <dependency>
                <groupId>com.jd.platform</groupId>
                <artifactId>asyncTool</artifactId>
                <version>1.3.1-SNAPSHOT</version>
            </dependency>
```
外网请使用jitpack.io上打的包
先添加repositories节点

```
        <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
然后添加如下maven依赖

```
        <dependency>
	    <groupId>com.gitee.jd-platform-opensource</groupId>
	    <artifactId>asyncTool</artifactId>
	    <version>V1.3-SNAPSHOT</version>
	</dependency>
```




#### 基本组件
worker：  一个最小的任务执行单元。通常是一个网络调用，或一段耗时操作。

T，V两个泛型，分别是入参和出参类型。

譬如该耗时操作，入参是String，执行完毕的结果是Integer，那么就可以用泛型来定义。

多个不同的worker之间，没有关联，分别可以有不同的入参、出参类型。

```
/**
 * 每个最小执行单元需要实现该接口
 * @author wuweifeng wrote on 2019-11-19.
 */
public interface IWorker<T, V> {
    /**
     * 在这里做耗时操作，如rpc请求、IO等
     *
     * @param object
     *         object
     */
    V action(T object, Map<String, WorkerWrapper> allWrappers);

    /**
     * 超时、异常时，返回的默认值
     * @return 默认值
     */
    V defaultValue();
}
```


callBack：对每个worker的回调。worker执行完毕后，会回调该接口，带着执行成功、失败、原始入参、和详细的结果。

```
/**
 * 每个执行单元执行完毕后，会回调该接口</p>
 * 需要监听执行结果的，实现该接口即可
 * @author wuweifeng wrote on 2019-11-19.
 */
public interface ICallback<T, V> {

    void begin();

    /**
     * 耗时操作执行完毕后，就给value注入值
     *
     */
    void result(boolean success, T param, WorkResult<V> workResult);
}

```

wrapper：组合了worker和callback，是一个 **最小的调度单元** 。通过编排wrapper之间的关系，达到组合各个worker顺序的目的。

wrapper的泛型和worker的一样，决定了入参和结果的类型。

```
        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "3", w3);

```

如

![输入图片说明](https://images.gitee.com/uploads/images/2019/1225/132251_b7cfac23_303698.png "屏幕截图.png")
    
  0执行完,同时1和2, 1\2都完成后3。3会等待2完成
  
此时，你可以定义一个 **worker** 

```
/**
 * @author wuweifeng wrote on 2019-11-20.
 */
public class ParWorker1 implements IWorker<String, String>, ICallback<String, String> {

    @Override
    public String action(String object) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "result = " + SystemClock.now() + "---param = " + object + " from 1";
    }

    @Override
    public String defaultValue() {
        return "worker1--default";
    }

    @Override
    public void begin() {
        //System.out.println(Thread.currentThread().getName() + "- start --" + System.currentTimeMillis());
    }

    @Override
    public void result(boolean success, String param, WorkResult<String> workResult) {
        if (success) {
            System.out.println("callback worker1 success--" + SystemClock.now() + "----" + workResult.getResult()
                    + "-threadName:" +Thread.currentThread().getName());
        } else {
            System.err.println("callback worker1 failure--" + SystemClock.now() + "----"  + workResult.getResult()
                    + "-threadName:" +Thread.currentThread().getName());
        }
    }

}

```
通过这一个类看一下，action里就是你的耗时操作，begin就是任务开始执行时的回调，result就是worker执行完毕后的回调。当你组合了多个执行单元时，每一步的执行，都在掌控之内。失败了，还会有自定义的默认值。这是CompleteableFuture无法做到的。


#### 安装教程

代码不多，直接拷贝包过去即可。

#### 使用说明

1.  3个任务并行

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/140256_8c015621_303698.png "屏幕截图.png")

```
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .build();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .build();

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(1500, workerWrapper, workerWrapper1, workerWrapper2);
//        Async.beginWork(800, workerWrapper, workerWrapper1, workerWrapper2);
//        Async.beginWork(1000, workerWrapper, workerWrapper1, workerWrapper2);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));
        System.out.println(Async.getThreadCount());

        System.out.println(workerWrapper.getWorkResult());
        Async.shutDown();
       
```


2.  1个执行完毕后，开启另外两个，另外两个执行完毕后，开始第4个

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/140405_93800bc7_303698.png "屏幕截图.png")

```
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();
        ParWorker3 w3 = new ParWorker3();

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("3")
                .build();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .next(workerWrapper3)
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .next(workerWrapper3)
                .build();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .next(workerWrapper1, workerWrapper2)
                .build();


        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(3100, workerWrapper);
//        Async.beginWork(2100, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(Async.getThreadCount());
        Async.shutDown();
```

如果觉得这样不符合左右的顺序，也可以用这种方式：

```
        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .build();

        WorkerWrapper<String, String> workerWrapper3 =  new WorkerWrapper.Builder<String, String>()
                .worker(w3)
                .callback(w3)
                .param("3")
                .build();

        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .depend(workerWrapper)
                .next(workerWrapper3)
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .depend(workerWrapper)
                .next(workerWrapper3)
                .build();
```



3. 复杂点的

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/140445_8d52e4d6_303698.png "屏幕截图.png")

在测试类里能找到，下图是执行结果。看时间戳，就知道执行的顺序。每个执行单元都是睡1秒。

![输入图片说明](https://images.gitee.com/uploads/images/2019/1225/133828_0c76624c_303698.png "屏幕截图.png")

4. 依赖别的worker执行结果作为入参

可以从action的参数中根据wrapper的id获取任意一个执行单元的执行结果，但请注意执行顺序，如果尚未执行，则在调用WorkerResult.getResult()会得到null！
![输入图片说明](https://images.gitee.com/uploads/images/2020/0511/215924_28af8655_303698.png "屏幕截图.png")![输入图片说明](https://images.gitee.com/uploads/images/2020/0511/215933_12e13dba_303698.png "屏幕截图.png")

5.  其他的详见test包下的测试类，支持各种形式的组合、编排。




