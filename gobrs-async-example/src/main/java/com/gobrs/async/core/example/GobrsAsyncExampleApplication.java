package com.gobrs.async.core.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类
 */
@SpringBootApplication
/**
 * 使用gobrs-async-test 模块创建的任务 为了方便不重复创建任务了
 */
@ComponentScan(value = {"com.gobrs.async.test", "com.gobrs.async.core.example"})
public class GobrsAsyncExampleApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GobrsAsyncExampleApplication.class, args);
    }

}
