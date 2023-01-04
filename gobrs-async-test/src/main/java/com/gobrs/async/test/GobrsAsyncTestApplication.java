package com.gobrs.async.test;

import com.gobrs.async.core.anno.EnabledMethodTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @program: gobrs-async
 * @ClassName GobrsAsyncTestApplication
 * @description:
 * @author: sizegang
 * @create: 2022-10-30
 **/
@EnabledMethodTask
@SpringBootApplication
public class GobrsAsyncTestApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GobrsAsyncTestApplication.class, args);
    }
}
