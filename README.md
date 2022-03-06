---
title: 简介 date: 2021-05-11 13:59:38 permalink: /pages/52d5c3 article: false
---

## 本框架是什么

**Gobrs-Async** 是一款功能强大、配置灵活、带有全链路异常回调、内存优化、异常状态管理于一身的高性能异步编排框架。为企业提供在复杂应用场景下动态任务编排的能力。
针对于复杂场景下，异步线程复杂性、任务依赖性、异常状态难控制性； **Gobrs-Async** 为此而生。

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

以上事例中，Product数据是通过RPC 方式获取， Item是通过HTTP服务获取，大家都知道， RPC性能要高于HTTP性能。 但是通过Future 的方式， get会阻塞等待 Item数据返回后才会往下执行。 这样的话，
图书音像、装修数据、限购数据等都要等待Item数据返回，但是这些中台并不依赖Item返回的数据， 所以会产生等待时间影响系统整体QPS。

## 业界对比

在开源平台找了挺多任务异步编排框架，发现都不是很理想，唯一一款现阶段比较好用的异步编排框架就数asyncTool比较好用。但是在使用时发现，API不是很好用。而且需要频繁的创建 <code>WorkerWrapper</code> 对象
用起来有点不爽。对于业务比较复杂的场景，在开发时需要写较多的 <code>WorkerWrapper</code> 代码，所以框架不能对全局异常进行拦截。只能通过任务的 <code>result</code> 方法捕捉单任务的异常。
不能在任意任务出现异常后 停止全局的异步任务。同时无法在发生全局异常的时候进行异常拦截。如果需要实现在发生需停止全局任务流的时候，发送报警邮件的功能。 asyncTool就显得力不从心了。

asyncTool 本身已经功能很强大了，本人与asyncTool 作者 都是在京东担任研发工作。 涉及到的场景大同小异。
会有很多业务场景，很复杂的中台接口调用关系。所以针对当前业务场景。需要探索更多的技术领域。本身技术是应该服务于业务，落地业务场景。

想给项目起一个简单易记的名字，类似于 Eureka、Nacos、Redis；经过再三考虑后，决定命名：**Gobrs-Async**

| 功能|  asyncTool   | Gobrs-Async  |
|----|  ----  | ----  |
| 多任务处理 | 是  | 是 |
|  单任务异常回调  | 是  | 是 |
| 全局异常中断 |否|是|
|可配置任务流|否|是|
|自定义异常拦截器|否|是|
|内存优化|否|是

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
> 摘自 [京东零售 asyncTool](https://gitee.com/jd-platform-opensource/asyncTool "京东零售技术asyncTool")


::: tip 应运而生 针对当前需求。Gobrs-Async 应运而生
:::

::: cardList

```yaml
- name: 技术小屋
  desc: 大道至简，知易行难
  avatar: https://cdn.jsdelivr.net/gh/xugaoyi/image_store/blog/20200122153807.jpg # 可选
  link: https://docs.sizegang.cn/ # 可选
  bgColor: '#CBEAFA' # 可选，默认var(--bodyBg)。颜色值有#号时请添加单引号
  textColor: '#6854A1' # 可选，默认var(--textColor)
- name: 架构师必经之路
  desc: '精品学习资源'
  avatar: https://cdn.jsdelivr.net/gh/xaoxuu/assets@master/avatar/avatar.png
  link: https://learn.sizegang.cn
  bgColor: '#718971'
  textColor: '#fff'
- name: 平凡的你我
  desc: 快乐购物，享受生活
  avatar: https://reinness.com/avatar.png
  link: https://m.jd.com
  bgColor: '#FCDBA0'
  textColor: '#A05F2C'
```

:::

## 它有什么特性

借鉴京东零售技术 asyncTool 框架，对框架中的对复杂API调用和频繁创建对象有了新的思考和感悟。在现有的情境下，Gobrs-Async 保留了asyncTool所有功能。同时
极简灵活的配置、全局自定义可中断全流程异常、内存优化、灵活的接入方式、提供SpringBoot Start 接入方式。更加考虑使用者的开发习惯。仅需要注入GobrsTask的Spring Bean 即可实现全流程接入。

Gobrs-Async 项目目录及其精简

- `gobrs-async-example`：Gobrs-Async 接入实例，提供测试用例。
- `gobrs-async-starter`：Gobrs-Async 框架核心组件
