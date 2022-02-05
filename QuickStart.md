## Gobrs-Async	教程



Gobrs-Async是一款简单方便的轻量级任务编排框架， 其可以对任务(task) 进行任意方式的组合。配置简单，适合快速上手开发任务编排需求。使用 <code>CompletableFuture</code> 设计开发。解决了各种复杂场景下 <code>CompletableFuture</code> 不支持场景。同时对任务异常 任务成功和失败都设计钩子函数。方便捕获异常信息。

项目已发布到  **中央仓库**

#### 基本组件

task：  一个最小的任务执行单元。通常是一个网络调用，或一段耗时操作。

T，V两个泛型，分别是入参和出参类型。

譬如该耗时操作，入参是String，执行完毕的结果是Integer，那么就可以用泛型来定义。

多个不同的task之间，没有关联，分别可以有不同的入参、出参类型。



#### 快速入门

##### 引入依赖

```sh
 <dependency>
   <groupId>io.github.memorydoc</groupId>
   <artifactId>gobrs-async-starter</artifactId>
   <version>1.0.3-RELEASE</version>
 </dependency>
```



此时，你可以定义一个 **TASK**

```
package com.jd.gobrs.gobrs.async.example.service;

import com.jd.gobrs.async.task.AsyncTask;
import com.jd.gobrs.async.task.TaskResult;
import com.jd.gobrs.async.wrapper.TaskWrapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: gobrs-async-example
 * @ClassName AService
 * @description:
 * @author: sizegang
 * @create: 2022-01-29 21:01
 * @Version 1.0
 **/
@Service
public class AService implements AsyncTask<String, Map> {

    @Override
    public Map doTask(String s, Map<String, TaskWrapper> map) {
        System.out.println("开始执行A");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("result", "I am is AService");
        return objectObjectHashMap;
    }

    @Override
    public boolean nessary(String s) {
        return true;
    }

    @Override
    public void result(boolean b, String s, TaskResult<Map> taskResult) {
        if (b) {
            System.out.println("AService success");
        } else {
            System.out.println("AService fail");
        }
    }
}

```

##### 配置

```sh
server:
  port: 8888
spring:
  gobrs:
    async:
      rules: '[{name: "test", content: "AService->EService->CService,BService->DService:not;AService->DService"}]'
```



#### 如何配置

1.  3个任务并行

![image-20220205131145351](/Users/sizegang1/Library/Application Support/typora-user-images/image-20220205131145351.png)

```sh
A;B;C
```



2. 任务串行

   ![image-20220205131501390](/Users/sizegang1/Library/Application Support/typora-user-images/image-20220205131501390.png)

   ```sh
   A->B->C
   ```





3. 1个执行完毕后，开启另外两个，另外两个执行完毕后，开始第4个

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/140405_93800bc7_303698.png "屏幕截图.png")

       ```sh 
A->B,C->F
```







4. 复杂点的

![输入图片说明](https://images.gitee.com/uploads/images/2019/1226/140445_8d52e4d6_303698.png "屏幕截图.png")

```sh
A->B,C->F->H;D->E->G->H
```

5. D 有两个执行通道，但是 谁最先执行完 就结束， 不全部等待两个管道全部执行完成

   ![image-20220205131950740](/Users/sizegang1/Library/Application Support/typora-user-images/image-20220205131950740.png)

   ```sh
   A->B->E->D:not; A->C->D
   ```

   如果C先执行完 那么 B-E-D 任务流则会停止

   ``` sh
   A->B->E->D:not; A->C->D:not
   ```

   两个任务流， 谁先执行完就先返回谁，另外的停止执行

5. 任务类查看 <code>gobrs-async-example</code>