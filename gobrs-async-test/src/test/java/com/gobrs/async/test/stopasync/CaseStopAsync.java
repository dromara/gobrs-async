package com.gobrs.async.test.stopasync;

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
 * @ClassName StopAsyncTest
 * @description:
 * @author: sizegang
 * @create: 2022-12-19
 **/
@SpringBootTest(classes = GobrsAsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseStopAsync {


    @Autowired(required = false)
    private GobrsAsync gobrsAsync;

    /**
     * Tcase.
     */
    @Test
    public void timeoutTest() {
        Map<Class, Object> params = new HashMap<>();
        AsyncResult asyncResult = gobrsAsync.go("stopAsyncRule", () -> params, 300000);
        System.out.println(asyncResult);
    }

}
