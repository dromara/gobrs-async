package com.gobrs.async.engine;

import com.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.gobrs.async.exception.NotTaskRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Optional;

/**
 * @author sizegang1
 * @program: gobrs-async
 * @ClassName RulePostProcessor
 * @description: Task flow resolver
 * The implementation ApplicationListener gets the Spring context, which in turn gets the Bean instance
 **/
public class RulePostProcessor implements ApplicationListener<ContextRefreshedEvent>  {
    Logger logger = LoggerFactory.getLogger(RulePostProcessor.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        GobrsAsyncProperties properties = applicationContext.getBean(GobrsAsyncProperties.class);
        String rules = properties.getRules();
        Optional.ofNullable(rules).map((data) -> {
            /**
             * The primary purpose of resolving a rule is to check that the rule is correct
             * Extensible task flow resolution up
             */
            RuleEngine engine = applicationContext.getBean(RuleEngine.class);
            engine.parse(data);
            logger.info("Gobrs Async Loading Success !!!");
            return 1;
        }).orElseThrow(() -> new NotTaskRuleException("spring.gobrs.async.rules is empty"));
    }
}
