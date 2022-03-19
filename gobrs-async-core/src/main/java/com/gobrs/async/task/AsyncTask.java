package com.gobrs.async.task;



/**
 * @program: gobrs-async-starter
 * @ClassName EventHandler
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/
public interface AsyncTask<Param, Result> extends Task{



    Result task(Param param);


    boolean nessary(Param param);

//    default <R> R getResult(GobrsAsyncSupport support, Class clazz, Class<R> resultClass) {
//        Map<String, TaskResult<R>> resultMap = support.getWorkResult();
//        TaskResult<R> rTaskResult = resultMap.get(clazz.getSimpleName()) != null ? resultMap.get(clazz.getSimpleName()) : resultMap.get(depKey(clazz));
//        if (rTaskResult != null) {
//            return rTaskResult.getResult();
//        }
//        return null;
//    }

}
