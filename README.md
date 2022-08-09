

## 资源索引
- [快速开始](https://async.sizegang.cn/pages/793dcb/#%E5%A6%82%E4%BD%95%E8%BF%90%E8%A1%8C-demo)
- [文档列表](https://async.sizegang.cn/pages/52d5c3/)
- [项目集成](https://async.sizegang.cn/pages/2f674a/)
- [加群沟通](https://async.sizegang.cn/pages/dd137d/)
## 本框架是什么

[**Gobrs-Async**](https://github.com/Memorydoc/gobrs-async) 是一款功能强大、配置灵活、带有全链路异常回调、内存优化、异常状态管理于一身的高性能异步编排框架。为企业提供在复杂应用场景下动态任务编排的能力。
针对于复杂场景下，异步线程复杂性、任务依赖性、异常状态难控制性； **Gobrs-Async** 为此而生。

## 能解决什么问题
能解决 `CompletableFuture` 所不能解决的问题。 怎么理解呢？

传统的`Future`、`CompleteableFuture`一定程度上可以完成任务编排，并可以把结果传递到下一个任务。如CompletableFuture有then方法，但是却无法做到对每一个执行单元的回调。譬如A执行完毕成功了，后面是B，我希望A在执行完后就有个回调结果，方便我监控当前的执行状况，或者打个日志什么的。失败了，我也可以记录个异常信息什么的。

此时，CompleteableFuture就无能为力了。

**Gobrs-Async**框架提供了这样的回调功能。并且，如果执行成功、失败、异常、超时等场景下都提供了管理线程任务的能力！


## 场景概述
### 场景一
![场景一](https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/gobrs-async/type1.png)

**说明**
任务A 执行完了之后，继续执行 B、C、D

### 场景二

![场景二](https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/gobrs-async/type2.png)

**说明**
任务A 执行完了之后执行B 然后再执行 C、D


### 场景三
![场景二](https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/gobrs-async/type3.png)

**说明**
任务A 执行完了之后执行B、E 然后按照顺序 B的流程走C、D、G。 E的流程走F、G

> **还有更多场景，如果你想详细理解任务编排的概念， 请仔细阅读文档，或者通过资源索引导航到官网了解全貌！**
## 为什么写这个项目

在开发复杂中台业务过程中，难免会遇到调用各种中台业务数据， 而且会出现复杂的中台数据依赖关系，在这种情况下。代码的复杂程度就会增加。 如下图所示：
![1.1](https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/oss/1141645973242_.pic.jpg)



在电商平台业务中， 各中台数据可能依赖 商品Product 数据，而且需要依赖特殊属性中 Item的数据。（有朋友会问，为什么Product 数据不和 Item数据出自同一个中台呢？中台业务发展是多样性的，不同业务中台设计方式不同 ，
难道我们就不对接了吗？所以我们要针对于这种复杂多变的中台业务数据提供技术支撑才是一个合格的开发者应该做的）而且Item数据是HTTP的服务，但Product 是RPC服务。 如果按照Future的 开发方式。我们可能会这样开发

```java 

    // 并行处理任务 Product 、 Item 的任务
    @Resource
    List<ParaExector> paraExectors;

    // 依赖于Product 和 Item的 任务
    @Resource
    List<SerExector> serExectors;

    public void testFuture(HttpServletRequest httpServletRequest) {
        DataContext dataContext = new DataContext();
        dataContext.setHttpServletRequest(httpServletRequest);
        List<Future> list = new ArrayList<>();
        for (AsyncTask asyncTask : paraExectors) {
            Future<?> submit = gobrsThreadPoolExecutor.submit(() -> {
                asyncTask.task(dataContext, null);
            });
            list.add(submit);
        }
        for (Future future : list) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        List<Future> ser = new ArrayList<>();
        for (AsyncTask asyncTask : serExectors) {
            Future<?> submit = gobrsThreadPoolExecutor.submit(() -> {
                asyncTask.task(dataContext, null);
            });
            ser.add(submit);
        }
        for (Future future : ser) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
```

### 存在的问题

以上示例中，Product数据是通过RPC 方式获取， Item是通过HTTP服务获取，大家都知道， RPC性能要高于HTTP性能。 但是通过Future 的方式， get会阻塞等待 Item数据返回后才会往下执行。 这样的话，
图书音像、装修数据、限购数据等都要等待Item数据返回，但是这些中台并不依赖Item返回的数据， 所以会产生等待时间影响系统整体QPS。


## 起源
![起源](https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/gobrs-async/gobrs-qy.png)

* 作者通过对开源中间件的源码详细阅读和二次开发的经验和使用心得总结而来。
* 用户的一些使用体验 包括业务的需求

## Gobrs-Async 核心能力
![核心能力](https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/gobrs-async/gobrs-hxnl.jpg)

## 业界对比

在开源平台找了挺多任务异步编排框架，发现都不是很理想，唯一一款现阶段比较好用的异步编排框架就数asyncTool比较好用。但是在使用时发现，API不是很好用。而且需要频繁的创建 <code>WorkerWrapper</code> 对象
用起来有点不爽。对于业务比较复杂的场景，在开发时需要写较多的 <code>WorkerWrapper</code> 代码，而且框架不能对全局异常进行拦截。只能通过任务的 <code>result</code> 方法捕捉单任务的异常。
不能在任意任务出现异常后 停止全局的异步任务。同时无法在发生全局异常的时候进行异常拦截。如果需要实现在发生需停止全局任务流的时候，发送报警邮件的功能。 asyncTool就显得力不从心了。

asyncTool 本身已经功能很强大了，本人与asyncTool 作者 都是在京东担任研发工作。 涉及到的场景大同小异。
会有很多业务场景，很复杂的中台接口调用关系。所以针对当前业务场景。需要探索更多的技术领域。本身技术是应该服务于业务，落地业务场景。

想给项目起一个简单易记的名字，类似于 Eureka、Nacos、Redis；经过再三考虑后，决定命名：**Gobrs-Async**

| 功能|  asyncTool   | Gobrs-Async  | sirector |
|----|  ----  | ----  | ---- |
| 多任务处理 | 是  | 是 | 是
|  单任务异常回调  | 是  | 是 | 否
| 全局异常中断 |否|是| 否
|可配置任务流|否|是| 否
|自定义异常拦截器|否|是| 否
|内存优化|否|是| 否
|可选的任务执行|否|是| 否

## 它解决了什么问题

在请求调用各大中台数据时，难免会出现多个中台数据互相依赖的情况，现实开发中会遇到如下场景。

并行常见的场景 1 客户端请求服务端接口，该接口需要调用其他N个微服务的接口

譬如 请求我的购物车，那么就需要去调用用户的rpc、商品详情的rpc、库存rpc、优惠券等等好多个服务。同时，这些服务还有相互依赖关系，譬如必须先拿到商品id后，才能去库存rpc服务请求库存信息。
最终全部获取完毕后，或超时了，就汇总结果，返回给客户端。

2 并行执行N个任务，后续根据这1-N个任务的执行结果来决定是否继续执行下一个任务

如用户可以通过邮箱、手机号、用户名登录，登录接口只有一个，那么当用户发起登录请求后，我们需要并行根据邮箱、手机号、用户名来同时查数据库，只要有一个成功了，都算成功，就可以继续执行下一步。而不是先试邮箱能否成功、再试手机号……

再如某接口限制了每个批次的传参数量，每次最多查询10个商品的信息，我有45个商品需要查询，就可以分5堆并行去查询，后续就是统计这5堆的查询结果。就看你是否强制要求全部查成功，还是不管有几堆查成功都给客户做返回

再如某个接口，有5个前置任务需要处理。其中有3个是必须要执行完毕才能执行后续的，另外2个是非强制的，只要这3个执行完就可以进行下一步，到时另外2个如果成功了就有值，如果还没执行完，就是默认值。

3 需要进行线程隔离的多批次任务。

如多组任务， 各组任务之间彼此不相关，每组都需要一个独立的线程池，每组都是独立的一套执行单元的组合。有点类似于hystrix的线程池隔离策略。

4 单机工作流任务编排。

5 其他有顺序编排的需求。

<br/>

## 它有什么特性

Gobrs-Async 在开发时考虑了众多使用者的开发喜欢，对异常处理的使用场景。并被运用到电商生产环境中，在京东经历这严酷的高并发考验。同时框架中
极简灵活的配置、全局自定义可中断全流程异常、内存优化、灵活的接入方式、提供SpringBoot Start 接入方式。更加考虑使用者的开发习惯。仅需要注入GobrsTask的Spring Bean 即可实现全流程接入。

Gobrs-Async 项目目录及其精简

- `gobrs-async-example`：Gobrs-Async 接入实例，提供测试用例。
- `gobrs-async-starter`：Gobrs-Async 框架核心组件





Gobrs-Async 在设计时，就充分考虑了开发者的使用习惯， 没有依赖任何中间件。 对并发框架做了良好的封装。主要使用
<code>CountDownLatch</code> 、<code>ReentrantLock</code> 、<code>volatile</code> 等一系列并发技术开发设计。

## 整体架构
<br/>

![1.0](https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/gobrs-async/gobrs-jgt3.png)

## 任务触发器

任务流的启动者， 负责启动任务执行流

## 规则解析引擎

负责解析使用者配置的规则，同时于Spring结合，将配置的 <code>Spring Bean</code> 解析成 <code>TaskBean</code>，进而通过解析引擎加载成 任务装饰器。进而组装成任务树

## 任务启动器

负责通过使用解析引擎解析的任务树。结合 **JUC** 并发框架调度实现对任务的统一管理，核心方法有
* trigger 触发任务加载器，为加载任务准备环境

## 任务加载器
负责加载任务流程，开始调用任务执行器执行核心流程

* load 核心任务流程方法，在这里阻塞等待整个任务流程
* getBeginProcess 获取子任务开始流程
* completed 任务完成
* errorInterrupted 任务失败 中断任务流程
* error 任务失败


### 任务执行器
最终的任务执行，每一个任务对应一个<code>TaskActuator</code> 任务的 拦截、异常、执行、线程复用 等必要条件判断都在这里处理
* prepare 任务前置处理
* preInterceptor 统一任务前置处理
* task 核心任务方法，业务执行内容
* postInterceptor 统一后置处理
* onSuccess 任务执行成功回调
* onFail 任务执行失败回调


## 任务总线
任务流程传递总线，包括 请求参数、任务加载器、 响应结果， 该对象暴露给使用者，拿到匹配业务的数据信息，例如： 返回结果、主动中断任务流程等功能
需要任务总线(<code>TaskSupport</code>)支持

## 核心类图
![核心类图](https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/gobrs-async/hxlt.jpg)

## 加群沟通
对于这个项目，是否有什么不一样看法，欢迎在 Issue 一起沟通交流；
群二维码七天会失效，可以添加作者微信进交流群

<table>
  <tr>
    <td align="center" style="width: 400px;">
      <a href="https://github.com/Memorydoc">
        <img src="https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/gobrs-async/1261646574221_.pic_hd.jpg?x-oss-process=image/resize,h_500,w_800" style="width: 400px;"><br>
        <sub></sub>
      </a><br>
    </td>
    <td align="center" style="width: 400px;">
      <a href="https://github.com/Memorydoc">
        <img src="https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/gobrs-async/1251646574128_.pic.jpg?x-oss-process=image/resize,h_500,w_800" style="width: 400px;"><br>
        <sub></sub>
      </a><br>
    </td>
  </tr>
</table>
