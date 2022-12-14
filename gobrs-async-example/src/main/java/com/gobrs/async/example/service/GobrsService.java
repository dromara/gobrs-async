package com.gobrs.async.example.service;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.core.config.GobrsAsyncRule;
import com.gobrs.async.core.config.GobrsConfig;
import com.gobrs.async.core.engine.RuleThermalLoad;
import com.gobrs.async.core.property.RuleConfig;
import com.gobrs.async.test.task.condition.AServiceCondition;
import com.gobrs.async.test.task.condition.CServiceCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The type Gobrs service.
 *
 * @program: gobrs -async-core
 * @ClassName GobrsService
 * @description:
 * @author: sizegang
 * @create: 2022 -03-28
 */
@Service
public class GobrsService {


    @Autowired(required = false)
    private GobrsAsync gobrsAsync;

    @Autowired(required = false)
    private RuleThermalLoad ruleThermalLoad;


    /**
     * Performance test.
     */
    public void performanceTest() {
        gobrsAsync.go("performanceRule", () -> "");
    }


    /**
     * Gobrs async.
     */
    public void gobrsAsync() {
        gobrsAsync.go("test", () -> new Object());
    }

    /**
     * Gobrs test async result.
     *
     * @return the async result
     */
    public AsyncResult gobrsTest() {
        Map<Class, Object> params = new HashMap<>();
        params.put(AServiceCondition.class, "1");
        params.put(CServiceCondition.class, "2");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AsyncResult resp = gobrsAsync.go("retryRule", () -> params);
        stopWatch.stop();
        System.out.println("cost" + stopWatch.getTotalTimeMillis());
        return resp;
    }

    /**
     * gobrsTest 可以使用 jmeter 一直访问着
     * 然后在浏览器调用 http://localhost:9999/gobrs/updateRule  看规则变更效果
     */
    public void updateRule() {
        GobrsAsyncRule r = new GobrsAsyncRule();
        r.setName("anyConditionGeneral");
        r.setContent("AService->CService->EService->GService; BService->DService->FService->HService;");
        ruleThermalLoad.load(r);

    }

    public void optionalProcess() {
        Map<Class, Object> params = new HashMap<>();
        Set<String> options = new HashSet<>();
        options.add("caseOptionalTaskD");
        AsyncResult asyncResult = gobrsAsync.go("optionalRule", () -> params, options, 300000);
    }
}
