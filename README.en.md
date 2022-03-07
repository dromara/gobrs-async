What is this framework

**Gobrs-Async** is a powerful, flexible configuration, with full link exception callback, memory optimization, exception state management in one of the high-performance asynchronous orchestration framework. It provides enterprises with the ability to dynamically arrange tasks in complex application scenarios.
In complex scenarios, asynchronous thread complexity, task dependence and abnormal state are difficult to control. ** Gobrs-async ** is born for this purpose.

## Why write this project

In the process of developing complex Taiwan business, it is inevitable to encounter the invocation of various Taiwan business data, and there will be complex Taiwan data dependency, in this case. The complexity of your code increases. As shown below:
! [1.1] (https://kevin-cloud-dubbo.oss-cn-beijing.aliyuncs.com/oss/1141645973242_.pic.jpg)

In the business of e-commerce platform, the data of each medium platform may depend on the Product data and the data of Item in the special attribute. (Some friends may ask, why do Product data and Item data come from the same middle platform? Zhongtai business development is diverse, different business zhongtai design methods are different,
Are we not going to dock? Therefore, what a qualified developer should do is to provide technical support for such complex and changeable business data in Taiwan.) And Item data is HTTP service, but Product is RPC service. If we follow the way Future is developed. We might develop it like this

```java

// process the Product and Item tasks in parallel
@Resource
List<ParaExector> paraExectors;

// Tasks that depend on Product and Item
@Resource
List<SerExector> serExectors;

public void testFuture(HttpServletRequest httpServletRequest) {
DataContext dataContext = new DataContext();
dataContext.setHttpServletRequest(httpServletRequest);
List<Future> list = new ArrayList<>();
for (AsyncTask asyncTask : paraExectors) {
Future<? > submit = gobrsThreadPoolExecutor.submit(() -> {
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
Future<? > submit = gobrsThreadPoolExecutor.submit(() -> {
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
` ` `

### existing problems

In the above example, Product data is obtained through RPC and Item is obtained through HTTP service. As we all know, RPC performance is higher than HTTP performance. With a Future, however, get blocks and waits for Item data to return before proceeding. In that case,
Book audio and video, decoration data, purchase limit data and so on all have to wait for Item data to return, but these middle stations do not depend on the data returned by Item, so the waiting time will affect the overall QPS of the system.

## Industry comparison

In the open source platform to find a lot of multi-task asynchronous orchestration framework, found not very ideal, the only asynchronous orchestration framework is relatively easy to use at this stage is the number of asyncTool is relatively easy to use. But when you use it, the API doesn't work very well. And you need to create <code>WorkerWrapper</code> objects frequently
It's a little uncomfortable to use. For complex scenarios, a lot of <code>WorkerWrapper</code> code needs to be written during development, so the framework cannot intercept global exceptions. Single-task exceptions can only be caught with the task's <code>result</code> method.
You cannot stop a global asynchronous task if any task is abnormal. It is also impossible to intercept global exceptions when they occur. If you need to implement the function of sending an alarm email when the global task flow needs to be stopped. AsyncTool falls short.

AsyncTool itself has very powerful functions. Both I and the author of asyncTool work in JINGdong. The scenarios involved are pretty much the same.
There will be a lot of business scenarios, very complex mid-platform interface call relationships. Therefore, it applies to the current business scenario. More technical areas need to be explored. The technology itself should serve the business and land the business scenario.

Wanted to give the project a name that was easy to remember, like Eureka, Nacos, Redis; After much consideration, we decided to name it: ** gobrs-async **

| | function asyncTool | Gobrs - Async |
|----|  ----  | ----  |
| is multitasking is | | |
| single task abnormal callback is | | |
Whether global abort | | | |
| | configurable task flow is | |
| custom exception is whether | | | interceptor
Is whether | | | memory optimization

## What problem does it solve

In the request to call each large medium data, it is inevitable that multiple medium data will depend on each other, and the following scenarios will be encountered in real development.

Parallel Common scenario 1 The client requests the server interface, which needs to invoke the interfaces of other N microservices

Asking for my shopping cart, for example, would require invoking user's RPC, item details RPC, inventory RPC, coupons, and many other services. At the same time, these services are interdependent. For example, the COMMODITY ID must be obtained before the inventory can be destocked. The RPC service requests inventory information.
When all the results are finally obtained, or a timeout expires, the results are summarized and returned to the client.

2 Perform N tasks in parallel, and determine whether to perform the next task based on the execution results of the 1-N tasks

If a user can log in by email, mobile phone number, or user name, there is only one login interface. After the user initiates a login request, the database needs to be searched based on the email, mobile phone number, and user name at the same time. If one of the login requests succeeds, the database is considered successful and the user can proceed to the next step. Instead of trying the email first and then the phone number...

Another example is that an interface limits the number of parameters to be transferred in each batch, and the information of a maximum of 10 commodities can be queried at a time. If I have 45 commodities to be queried, I can query them in parallel in 5 heaps, and then the query results of these 5 heaps will be counted. It depends on whether you force all checks to succeed, or whether you return a few checks to the customer

For an interface, there are five pre-tasks to be handled. Three of them must be completed before the subsequent execution, and the other two are optional. You can proceed to the next step as long as the three are completed. Then the other two will have values if they are successful, and the default values if they are not completed.

3 Multi-batch tasks requiring thread isolation.

For example, multiple groups of tasks are unrelated to each other. Each group requires an independent thread pool, and each group is a combination of an independent set of execution units. Similar to Hystrix's thread pool isolation policy.

4. Single-machine workflow task scheduling.

5. Other requirements for sequential arrangement.


What features does it have

Drawing lessons from THE asyncTool framework of JD retail technology, I have new thoughts and understandings on complex API calls and frequent object creation in the framework. In the current situation, gobrs-Async retains all the functions of asyncTool. At the same time
Simple and flexible configuration, global customization can interrupt the whole process exception, memory optimization, flexible access mode, SpringBoot Start access mode. More consideration of user development habits. All you need to do is inject GobrsTask's Spring beans for full process access.

Gobrs-async project directory and its compact

- 'gobrs-async-example' : gobrs-async access instances, providing test cases.
- 'gobrs-async-starter' : gobrs-async framework core component


[quick combat] (https://async.sizegang.cn)