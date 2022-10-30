package com.gobrs.async.test;

import com.gobrs.async.core.engine.RuleThermalLoad;
import com.gobrs.async.core.config.RuleConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The type Case thread pool com.gobrs.async.config.
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
     * Update com.gobrs.async.rule.
     *
     * @param rule the com.gobrs.async.rule
     */
    @Test
    public void updateRule(RuleConfig rule) {
        RuleConfig r = new RuleConfig();
        r.setName("ruleName");
        r.setContent("AService->CService->EService->GService; BService->DService->FService->HService;");
        ruleThermalLoad.load(rule);
    }
}
