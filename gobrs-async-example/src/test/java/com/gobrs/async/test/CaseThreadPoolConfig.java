package com.gobrs.async.test;

import com.gobrs.async.engine.RuleThermalLoad;
import com.gobrs.async.rule.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The type Case thread pool config.
 *
 * @program: gobrs -async
 * @ClassName CaseThreadPoolConfig
 * @description:
 * @author: sizegang
 * @create: 2022 -09-28
 */
public class CaseThreadPoolConfig {

    @Autowired(required = false)
    private RuleThermalLoad ruleThermalLoad;


    /**
     * Update rule.
     *
     * @param rule the rule
     */
    @Test
    public void updateRule(Rule rule) {
        Rule r = new Rule();
        r.setName("ruleName");
        r.setContent("AService->CService->EService->GService; BService->DService->FService->HService;");
        ruleThermalLoad.load(rule);
    }
}
