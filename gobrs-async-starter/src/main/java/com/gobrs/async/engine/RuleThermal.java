package com.gobrs.async.engine;
import com.gobrs.async.rule.Rule;
import java.util.List;

/**
 * The interface Rule thermal.
 *
 * @program: gobrs -async-core
 * @ClassName RuleFacade
 * @description:
 * @author: sizegang
 * @create: 2022 -04-08
 */
public interface RuleThermal {


    /**
     * Load.
     *
     * @param rule the rule
     */
    void load(Rule rule);

    /**
     * Load.
     *
     * @param ruleList the rule list
     */
    void load(List<Rule> ruleList);


}
