package com.jd.gobrs.async.engine;

import com.jd.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.jd.gobrs.async.exception.NotTaskRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Optional;

/**
 * @author sizegang1
 * @program: gobrs-async
 * @ClassName GobrsEngineLoading
 * @description:
 * @date 2022-01-27 22:05
 **/
public class RulePostProcessor implements ApplicationListener<ContextRefreshedEvent> {
    Logger logger = LoggerFactory.getLogger(RulePostProcessor.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ApplicationContext applicationContext = event.getApplicationContext();
        GobrsAsyncProperties properties = applicationContext.getBean(GobrsAsyncProperties.class);
        String rules = properties.getRules();
        Optional.ofNullable(rules).map((data) -> {
            // 初始化解析规则 主要是为了检查规则是否正确
            RuleParse engine = applicationContext.getBean(RuleParse.class);
            engine.parse(data);
            logger.info("Gobrs Async Loading Success !!!");
            return 1;
        }).orElseThrow(() -> new NotTaskRuleException("spring.gobrs.async.rules is empty"));
    }
}