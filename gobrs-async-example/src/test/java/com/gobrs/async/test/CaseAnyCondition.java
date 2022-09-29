package com.gobrs.async.test;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.example.GobrsAsyncExampleApplication;
import com.gobrs.async.example.task.condition.AServiceCondition;
import com.gobrs.async.example.task.condition.CServiceCondition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The type Case any condition.
 *
 * @program: gobrs -async
 * @ClassName CaseAnyCondition
 * @description:
 * @author: sizegang
 * @create: 2022 -09-28
 */
@SpringBootTest(classes = GobrsAsyncExampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseAnyCondition {

    @Autowired(required = false)
    private GobrsAsync gobrsAsync;

    /**
     * Test condition.
     */
    @Test
    public void testCondition() {
        Set<String> cases = new HashSet<>();
        cases.add("BService");
        cases.add("GService");

        Map<Class, Object> params = new HashMap<>();
        params.put(AServiceCondition.class, "1");
        params.put(CServiceCondition.class, "2");

        AsyncResult result = gobrsAsync.go("anyConditionRule", () -> params, 300000);
    }

    @Test
    public void testConditionAppend() {
        Set<String> cases = new HashSet<>();
        cases.add("BService");
        cases.add("GService");

        Map<Class, Object> params = new HashMap<>();
        params.put(AServiceCondition.class, "1");
        params.put(CServiceCondition.class, "2");

        AsyncResult result = gobrsAsync.go("anyConditionRuleAppend", () -> params, 300000);
    }
}
