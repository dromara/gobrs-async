package com.gobrs.async.core.engine;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.GobrsPrint;
import com.gobrs.async.core.config.ConfigManager;
import com.gobrs.async.core.config.GobrsAsyncProperties;
import com.gobrs.async.core.common.exception.NotTaskRuleException;
import com.gobrs.async.core.rule.Rule;
import com.gobrs.async.core.holder.BeanHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.Optional;

/**
 * The type Rule post processor.
 *
 * @author sizegang1
 * @program: gobrs -async
 * @ClassName RulePostProcessor
 * @description: Task flow resolver The implementation ApplicationListener gets the Spring context, which in turn gets the Bean instance
 */
public class RulePostProcessor implements ApplicationListener<ApplicationReadyEvent> {
    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(RulePostProcessor.class);

    private ConfigManager configManager;

    public RulePostProcessor(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        GobrsAsyncProperties properties = BeanHolder.getBean(GobrsAsyncProperties.class);
        GobrsAsync gobrsAsync = BeanHolder.getBean(GobrsAsync.class);

        List<Rule> rules = properties.getRules();
        Optional.ofNullable(rules).map((data) -> {
            /**
             * The primary purpose of resolving a com.gobrs.async.rule is to check that the com.gobrs.async.rule is correct
             * Extensible com.gobrs.async.com.gobrs.async.test.task flow resolution up
             *
             *  recommend : Custom rules com.gobrs.async.engine can be extended using SPI
             */

            try {
                RuleEngine engine = BeanHolder.getBean(RuleEngine.class);
                for (Rule rule : rules) {
                    configManager.addRule(rule.getName(), rule);
                    engine.doParse(rule, false);
                    gobrsAsync.readyTo(rule.getName());
                }
            } catch (Exception exception) {
                logger.error("RulePostProcessor parse error{}", exception);
                throw exception;
            }
            GobrsPrint.printBanner();
            GobrsPrint.getVersion();
            return 1;
        }).orElseThrow(() -> new NotTaskRuleException("com.gobrs.async.rule parse error"));
    }


}
