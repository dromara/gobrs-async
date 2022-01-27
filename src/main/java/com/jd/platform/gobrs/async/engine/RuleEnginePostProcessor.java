package com.jd.platform.gobrs.async.engine;

import com.alibaba.fastjson.JSONObject;
import com.jd.platform.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.jd.platform.gobrs.async.exception.NotTaskRuleException;
import com.jd.platform.gobrs.async.rule.Rule;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Optional;

/**
 * @author sizegang1
 * @program: gobrs-async
 * @ClassName GobrsEngineLoading
 * @description:
 * @date 2022-01-27 15:02
 **/
public class RuleEnginePostProcessor implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ApplicationContext applicationContext = event.getApplicationContext();
        GobrsAsyncProperties properties = applicationContext.getBean(GobrsAsyncProperties.class);
        String rules = properties.getRules();
        Optional.ofNullable(rules).map((data) -> {
            // 初始化解析规则 主要是为了检查规则是否正确
            RuleParseEngine engine = applicationContext.getBean(RuleParseEngine.class);
            engine.parse(data);
            return null;
        }).orElseThrow(() -> {
            return new NotTaskRuleException("spring.gobrs.async.rules is empty");
        });

    }
}
