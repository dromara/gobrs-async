package com.gobrs.async.test.retry;

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
 * @ClassName CaseTimeout
 * @description:
 * @author: sizegang
 * @create: 2022-12-09
 **/
@SpringBootTest(classes = GobrsAsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseTimeout {


    @Autowired(required = false)
    private GobrsAsync gobrsAsync;

    /**
     * Tcase.
     */
    @Test
    public void timeoutTest() {
        Map<Class, Object> params = new HashMap<>();
        AsyncResult asyncResult = gobrsAsync.go("retryRule", () -> params, 300000);
    }

}
