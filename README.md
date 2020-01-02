# 并发框架说明
有问题可以给作者发邮件说明，或者有特定的场景需求，感谢您的意见。wuweifeng10@jd.com

有对区块链感兴趣的，可以参考作者另一个[GVP项目](https://gitee.com/tianyalei/md_blockchain)，java区块链底层入门

如果只是需要用这个框架，请往下看即可。如果需要深入了解这个框架是如何一步一步实现的，从接到需求，到每一步的思考，每个类为什么这么设计，为什么有这些方法，也就是如何从0到1开发出这个框架，我在[csdn开了专栏](https://blog.csdn.net/tianyaleixiaowu/category_9637010.html)专门讲中间件如何从0开发，包括并不限于这个小框架。京东内部同事可在cf上搜索我erp也能看到。

#### 并发常见的场景
1 客户端请求服务端接口，该接口需要调用其他N个微服务的接口。譬如 请求我的订单，那么就需要去调用用户的rpc、商品详情的rpc、库存rpc、优惠券等等好多个服务。同时，这些服务还有相互依赖关系，譬如必须先拿到用户的某个字段后，再去某rpc服务请求数据。 最终全部获取完毕后，或超时了，就汇总结果，返回给客户端。

2 工作流式的很多个任务

3 爬虫之类的，有前后依赖关系

#### 并发场景可能存在的需求之——任意编排
1 多个执行单元的串行请求

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/092905_55771221_303698.png "屏幕截图.png")

2 多个执行单元的并行请求

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/092925_060c01a5_303698.png "屏幕截图.png")

3 阻塞等待，串行的后面跟多个并行

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/092935_5babe488_303698.png "屏幕截图.png")

4 阻塞等待，多个并行的执行完毕后才执行某个

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/092952_c5647879_303698.png "屏幕截图.png")

5 串并行相互依赖

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/093006_d8cd133c_303698.png "屏幕截图.png")

6 复杂场景

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/093023_357a2912_303698.png "屏幕截图.png")

#### 并发场景可能存在的需求之——每个执行结果的回调
传统的Future、CompleteableFuture一定程度上可以完成任务编排，并可以把结果传递到下一个任务。如CompletableFuture有then方法，但是却无法做到对每一个执行单元的回调。譬如A执行完毕成功了，后面是B，我希望A在执行完后就有个回调结果，方便我监控当前的执行状况，或者打个日志什么的。失败了，我也可以记录个异常信息什么的。

此时，传统的就无能为力了。

我的框架提供了这样的回调功能。并且，如果执行失败、超时，可以在定义这个执行单元时就设定默认值。

#### 并发场景可能存在的需求之——执行顺序的强依赖和弱依赖
如上图的3，A和B并发执行，最后是C。

有些场景下，我们希望A和B都执行完毕后，才能执行C，CompletableFuture里有个allOf(futures...).then()方法可以做到。

有些场景下，我们希望A或者B任何一个执行完毕，就执行C，CompletableFuture里有个anyOf(futures...).then()方法可以做到。

我的框架同样提供了类似的功能，通过设定wrapper里的addDepend依赖时，可以指定依赖的任务是否must执行完毕。如果依赖的是must要执行的，那么就一定会等待所有的must依赖项全执行完毕，才执行自己。

如果依赖的都不是must，那么就可以任意一个依赖项执行完毕，就可以执行自己了。

#### 并发场景可能存在的需求之——依赖上游的执行结果作为入参
譬如A-B-C三个执行单元，A的入参是String，出参是int，B呢它需要用A的结果作为自己的入参。也就是说A、B并不是独立的，而是有结果依赖关系的。

在A执行完毕之前，B是取不到结果的，只是知道A的结果类型。

那么，我的框架也支持这样的场景。可以在编排时，就取A的结果包装类，作为B的入参。虽然此时尚未执行，必然是空，但可以保证A执行完毕后，B的入参会被赋值。

#### 并发场景可能存在的需求之——全组任务的超时
一组任务，虽然内部的各个执行单元的时间不可控，但是我可以控制全组的执行时间不超过某个值。通过设置timeOut，来控制全组的执行阈值。

#### 并发场景可能存在的需求之——高性能、低线程数
该框架全程无锁，没有一个加锁的地方。

创建线程量少。![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/093227_9633e2a8_303698.png "屏幕截图.png")
如这样的，A会运行在B、C执行更慢的那个单元的线程上，而不会额外创建线程。
#### async-Tool介绍
解决任意的多线程并行、串行、阻塞、依赖、回调的并发框架，可以任意组合各线程的执行顺序，还带全链路回调和超时控制。

其中的A、B、C分别是一个最小执行单元（worker），可以是一段耗时代码、一次Rpc调用等，不局限于你做什么。

该框架，可以将这些worker，按照你想要的各种执行顺序，加以组合编排。最终得到结果。

并且，该框架 **为每一个worker都提供了执行结果的回调和执行失败后自定义默认值** 。譬如A执行完毕后，A的监听器会收到回调，带着A的执行结果（成功、超时、异常）。

根据你的需求，将各个执行单元组合完毕后，开始在主线程执行并阻塞，直到最后一个执行完毕。并且 **可以设置全组的超时时间** 。

 **该框架支持后面的执行单元以前面的执行单元的结果为自己的入参** 。譬如你的执行单元B的入参是ResultA，ResultA就是A的执行结果，那也可以支持。在编排时，就可以预先设定B或C的入参为A的result，即便此时A尚未开始执行。当A执行完毕后，自然会把结果传递到B的入参去。

 **该框架全程无锁。** 



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
    V action(T object);

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

        workerWrapper.addNext(workerWrapper1, workerWrapper2);
        workerWrapper1.addNext(workerWrapper3);
        workerWrapper2.addNext(workerWrapper3);
```

如

![输入图片说明](https://images.gitee.com/uploads/images/2019/1225/132251_b7cfac23_303698.png "屏幕截图.png")
    
  0执行完,同时1和2, 1\2都完成后3。3会等待2完成
  
譬如，你可以定义一个 **worker** 

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

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(1500, workerWrapper, workerWrapper1, workerWrapper2);
//        Async.beginWork(800, workerWrapper, workerWrapper1, workerWrapper2);
//        Async.beginWork(1000, workerWrapper, workerWrapper1, workerWrapper2);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));
        System.out.println(getThreadCount());

        System.out.println(workerWrapper.getWorkResult());
//        System.out.println(getThreadCount());
        Async.shutDown();
```



2.  1个执行完毕后，开启另外两个，另外两个执行完毕后，开始第4个

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/140405_93800bc7_303698.png "屏幕截图.png")

```
        ParWorker w = new ParWorker();
        ParWorker1 w1 = new ParWorker1();
        ParWorker2 w2 = new ParWorker2();
        w2.setSleepTime(2000);
        ParWorker3 w3 = new ParWorker3();

        WorkerWrapper<String, String> workerWrapper = new WorkerWrapper<>(w, "0", w);
        WorkerWrapper<String, String> workerWrapper1 = new WorkerWrapper<>(w1, "1", w1);
        WorkerWrapper<String, String> workerWrapper2 = new WorkerWrapper<>(w2, "2", w2);
        WorkerWrapper<String, String> workerWrapper3 = new WorkerWrapper<>(w3, "3", w3);

        workerWrapper.addNext(workerWrapper1, workerWrapper2);
        workerWrapper1.addNext(workerWrapper3);
        workerWrapper2.addNext(workerWrapper3);

        long now = SystemClock.now();
        System.out.println("begin-" + now);

        //正常完毕
        Async.beginWork(4100, workerWrapper);
        //3会超时
//        Async.beginWork(3100, workerWrapper);
        //2,3会超时
//        Async.beginWork(2900, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));

        System.out.println(getThreadCount());
        Async.shutDown();
```

3. 复杂点的

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/140445_8d52e4d6_303698.png "屏幕截图.png")

在测试类里能找到，下图是执行结果。看时间戳，就知道执行的顺序。每个执行单元都是睡1秒。

![输入图片说明](https://images.gitee.com/uploads/images/2019/1225/133828_0c76624c_303698.png "屏幕截图.png")

4.  其他的详见test包下的测试类，支持各种形式的组合、编排。




