package com.gobrs.async.engine;
import com.gobrs.async.rule.Rule;
import java.util.List;

/**
 * @program: gobrs-async-core
 * @ClassName RuleFacade
 * @description:
 * @author: sizegang
 * @create: 2022-04-08
 **/

public interface RuleThermal {


    void load(Rule rule);

    void load(List<Rule> ruleList);


}
