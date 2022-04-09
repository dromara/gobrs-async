package com.gobrs.async.engine;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: gobrs-async-core
 * @ClassName ThermalLoading
 * @description:
 * @author: sizegang
 * @create: 2022-04-07
 **/

public class RuleThermalLoad implements RuleThermal {

    Logger logger = LoggerFactory.getLogger(RuleThermalLoad.class);
    @Resource
    private RuleEngine ruleEngine;

    @Resource
    private GobrsAsync gobrsAsync;

    @Override
    public void load(Rule rule) {
        try {
            ruleEngine.doParse(rule, true);
            gobrsAsync.readyTo(rule.getName(), true);
            logger.info("rule {} update success", rule.getName());
        } catch (Exception ex) {
            logger.error("rule {} update fail {}", rule.getName(), ex);
        }
    }


    @Override
    public void load(List<Rule> ruleList) {
        ruleList.stream().parallel().forEach(x -> {
            try {
                ruleEngine.doParse(x, true);
                gobrsAsync.readyTo(x.getName(), true);
                logger.info("rule {} update success", x.getName());
            } catch (Exception ex) {
                logger.error("rule {} update fail {}", x.getName(), ex);
            }
        });
    }
}
