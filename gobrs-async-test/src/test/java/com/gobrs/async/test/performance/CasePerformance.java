package com.gobrs.async.test.performance;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.test.GobrsAsyncTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
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
public class CasePerformance {

    @Resource
    private GobrsAsync gobrsAsync;

    /**
     * Tcase.
     */
    @Test
    public void performanceTest() {
        long start = System.currentTimeMillis();
        gobrsAsync.go("performance", () -> "args");
        System.out.println("耗时" + (System.currentTimeMillis() - start));
    }

}
