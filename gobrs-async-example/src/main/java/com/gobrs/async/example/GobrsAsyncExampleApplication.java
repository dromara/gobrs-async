package com.gobrs.async.example;

import com.gobrs.async.plugin.trace.GobrsLogger;
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
@ComponentScan(value = {"com.gobrs.async"})
public class GobrsAsyncExampleApplication {

    /**
     * 集成tlog 日志打印框架 官网: https://tlog.yomahub.com/
     */
    static {
        GobrsLogger.logger();
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GobrsAsyncExampleApplication.class, args);
    }

}
