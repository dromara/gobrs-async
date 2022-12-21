package com.gobrs.async.test.interrupt;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.test.GobrsAsyncTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName InterruptRule
 * @description:
 * @author: sizegang
 * @create: 2022-11-02
 **/
@SpringBootTest(classes = GobrsAsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InterruptRule {
    @Autowired(required = false)
    private GobrsAsync gobrsAsync;
    @Test
    public void testInterruptRule() {
        Map<Class, Object> params = new HashMap<>();

        AsyncResult result = gobrsAsync.go("taskInterrupt", () -> params, 300000);
        System.out.println(result);

    }
}
