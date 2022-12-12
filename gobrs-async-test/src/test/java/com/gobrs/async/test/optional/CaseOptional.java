package com.gobrs.async.test.optional;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.test.GobrsAsyncTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @program: gobrs-async
 * @ClassName CaseTimeout
 * @description:
 * @author: sizegang
 * @create: 2022-12-09
 **/
@SpringBootTest(classes = GobrsAsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseOptional {


    @Autowired(required = false)
    private GobrsAsync gobrsAsync;

    /**
     * Tcase.
     */
    @Test
    public void testOptional() {
        Map<Class, Object> params = new HashMap<>();
        Set<String> options = new HashSet<>();
        options.add("caseOptionalTaskD");
        AsyncResult asyncResult = gobrsAsync.go("optionalRule", () -> params, options, 300000);
    }

}
