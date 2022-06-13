package com.gobrs.async.example.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gobrs.async.autoconfig.GobrsAsyncProperties;
import com.gobrs.async.engine.RuleEngine;

/**
 * @author azh
 * @createDate 2022/6/12  22:23
 * @Version 1.0.0
 * @Context:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RuleEngineTest {

    @Value("${server.port}")
    private int port;

//    @Value("${spring.gobrs.async.rules}")
//    private String ruleStrings;

//    @Autowired
//    private Rules rules;

    @Autowired
    private GobrsAsyncProperties gobrsAsyncProperties;

    @Autowired
    private RuleEngine engine;

    @Test
    public void testRule() {
//        System.out.println(rules.getRules());
//        engine.parse(ruleString);
        System.out.println(gobrsAsyncProperties.getRules());
//        System.out.println(gobrsAsyncProperties.getPoint());
    }

}
