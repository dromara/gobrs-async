package com.gobrs.async.core.engine;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.config.GobrsAsyncRule;
import com.gobrs.async.core.config.GobrsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * The type Rule thermal load.
 *
 * @program: gobrs -async-core
 * @ClassName ThermalLoading
 * @description:
 * @author: sizegang
 * @create: 2022 -04-07
 */
public class RuleThermalLoad implements RuleThermal {

    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(RuleThermalLoad.class);
    @Resource
    private RuleEngine ruleEngine;

    @Resource
    private GobrsAsync gobrsAsync;

    @Override
    public void load(GobrsAsyncRule rule) {
        try {
            ruleEngine.doParse(rule, true);
            gobrsAsync.readyTo(rule.getName(), true);
            logger.info("gobrs.async.rule {} update success", rule.getName());
        } catch (Exception ex) {
            logger.error("com.gobrs.async.rule {} update fail {}", rule.getName(), ex);
        }
    }


    @Override
    public void load(List<GobrsAsyncRule> ruleList) {
        ruleList.stream().parallel().forEach(x -> {
            try {
                ruleEngine.doParse(x, true);
                gobrsAsync.readyTo(x.getName(), true);
                logger.info("gobrs.async.rule {} update success !!!", x.getName());
            } catch (Exception ex) {
                logger.error("gobrs.async.rule {} update fail", x.getName(), ex);
            }
        });
    }
}
