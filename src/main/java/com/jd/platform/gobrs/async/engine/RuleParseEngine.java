package com.jd.platform.gobrs.async.engine;

import com.jd.platform.gobrs.async.rule.Rule;
import com.jd.platform.gobrs.async.wrapper.TaskWrapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: gobrs-async
 * @ClassName RuleParseEngine
 * @description:
 * @author: sizegang
 * @create: 2022-01-26 01:55
 * @Version 1.0
 **/
public class RuleParseEngine extends AbstractEngine{
    @Override
    public List<TaskWrapper> parsing(Rule r) {
        String content = r.getContent();
        // todo 解析规则

        return null;
    }
}
