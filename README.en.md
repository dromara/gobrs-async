##Concurrent framework description

 If you have any questions, you can send an email to the author, or you can have specific scenario requirements. Thank you for your comments. wuweifeng10@jd.com



 If you are interested in blockchain, please refer to another [GVP project] (https://gitee.com/tianalei/md_blockchain) of the author, Java blockchain low level introduction



 If you just need to use this frame, please look down. If you need to have a deep understanding of how this framework is implemented step by step, from receiving the requirements to thinking about each step, why each class is so designed, why there are these methods, that is, how to develop this framework from 0 to 1, I opened a column in [CSDN] (https://blog.csdn.net/tianaleixiaowu/category_. HTML) to talk about how middleware is developed from 0, including and Not limited to this small frame. JD internal colleagues can search my ERP on CF and see it.



 ####To do

 Recently, many students have reported that they want to support dynamic task arrangement based on file configuration. Considering this, we need to add an additional plug-in function. At present, the function does not need to be changed. However, the format of the file, how to accurately express the sequence, parallel sequence and dependency, is relatively complex. If there are related needs and interests, we can study it.



 ####Concurrent common scenarios

 1. The client requests the server interface, which needs to call the interfaces of other n microservices. For example, to request my order, you need to call the user's RPC, product details RPC, inventory RPC, coupons and many other services. At the same time, these services also have interdependence, for example, you must first get a user's field, and then go to an RPC service to request data. After all the results are finally obtained or timeout, the result will be summarized and returned to the client.



 2. Many tasks in the form of Workflow



 3. Reptiles and so on. They are dependent on each other



 ####Possible requirements for concurrent scenarios -- arbitrary arrangement

 1 serial request of multiple execution units



 ! [enter picture description] (https://images.gitee.com/uploads/images/2019/1226/092905. PNG "screenshot. PNG")



 2 parallel requests of multiple execution units



 ! [enter picture description] (https://images.gitee.com/uploads/images/2019/1226/092925 c01a5_. PNG "screenshot. PNG")



 3 block wait, serial followed by multiple parallels



 ! [enter picture description] (https://images.gitee.com/uploads/images/2019/1226/092935 "[5babe488" [303698. PNG "screenshot. PNG")



 4 block wait, execute a certain one after multiple parallel execution



 ! [enter picture description] (https://images.gitee.com/uploads/images/2019/1226/092952. PNG "screenshot. PNG")



 5 series parallel interdependence



 ! [enter picture description] (https://images.gitee.com/uploads/images/2019/1226/093006 cd133c. PNG "screenshot. PNG")



 6 complex scene



 ! [enter picture description] (https://images.gitee.com/uploads/images/2019/1226/093023 "screenshot. PNG")



 ####Possible requirements for concurrent scenarios -- callbacks for each execution result

 Traditional future and completeablefuture can complete task choreography to some extent, and transfer the results to the next task. For example, completabilefuture has the then method, but it can't do the callback for each execution unit. For example, if the execution of a is successful, followed by B, I hope that a will have a callback result after the execution, so that I can monitor the current execution status, or log something. Failed, I can also record an exception information or something.



 At this time, the traditional can do nothing.

 
 My framework provides such a callback function. In addition, if the execution fails or times out, the default value can be set when defining the execution unit.



 ####The possible requirements of concurrent scenarios -- strong and weak dependence on execution order

 As shown in Figure 3, a and B execute concurrently, and finally C.



 In some scenarios, we want to execute C only after a and B have finished executing. There is an allow (futures...). Then () method in completable future.



 In some scenarios, we want either a or B to finish execution, and then execute C. There is an anyof (futures...). Then () method in completable future.



 My framework also provides similar functions. When setting the adddepend dependency in the wrapper, you can specify whether the dependent task must be completed. If you depend on the execution of the must, you must wait for all the must dependencies to be completed before executing yourself.



 If you don't rely on must, you can execute any dependency and then you can execute yourself.



 ####Possible requirements for concurrent scenarios -- relying on upstream execution results as input parameters

 For example, for the three execution units of a-b-c, the input parameter of a is string, and the output parameter is int. for B, it needs to use the result of a as its input parameter. In other words, a and B are not independent but result dependent.



 Before a finishes executing, B can't get the result. It just knows the result type of A.



 So, my framework also supports such scenarios. You can take the result wrapper class of a as the input parameter of B when arranging. Although it is not implemented at this time, it must be empty, but it can ensure that after the implementation of a, the participation of B will be assigned.



 ####Possible requirements for concurrent scenarios -- timeout of the whole group of tasks

 For a group of tasks, although the time of each internal execution unit is not controllable, I can control that the execution time of the whole group does not exceed a certain value. Control the execution threshold of the whole group by setting timeout.
