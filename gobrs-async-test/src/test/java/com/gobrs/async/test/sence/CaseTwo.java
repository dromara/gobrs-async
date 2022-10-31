package com.gobrs.async.test.sence;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.test.GobrsAsyncTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName CaseOne
 * @description:
 * @author: sizegang
 * @create: 2022-10-31
 **/
@SpringBootTest(classes = GobrsAsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseTwo {

    /**
     * A -> B,C,D
     */

    @Autowired
    private GobrsAsync gobrsAsync;


    @Test
    public void caseTwo() {
        Map<String, Object> params = new HashMap<>();
        gobrsAsync.go("caseTwo", () -> params);

    }

}
