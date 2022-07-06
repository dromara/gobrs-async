package com.gobrs.async.example.controller;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.example.service.GobrsService;
import com.gobrs.async.example.task.AService;
import com.gobrs.async.rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Gobrs controller.
 *
 * @program: gobrs -async-core
 * @ClassName GobrsController
 * @description: Controller
 * @author: sizegang
 * @create: 2022 -03-20
 */
@RestController
@RequestMapping("gobrs")
public class GobrsController {

    @Autowired
    private GobrsAsync gobrsAsync;

    @Autowired
    private GobrsService gobrsService;

    /**
     * Gobrs test string.
     *
     * @return the string
     */
    @RequestMapping("testGobrs")
    public String gobrsTest() {
        Map<Class, Object> params = new HashMap<>();
        params.put(AService.class, "A的参数");
        AsyncResult test = gobrsAsync.go("test", () -> params);
        return "success";
    }


    /**
     * Future.
     */
    @RequestMapping("future")
    public void future() {
        long start = System.currentTimeMillis();
        gobrsService.future();

        long coust = System.currentTimeMillis() - start;
        System.out.println("future " + coust);

    }


    /**
     * Sets gobrs async.
     */
    @RequestMapping("gobrsAsync")
    public void setGobrsAsync() {
        //开始时间: 获取当前时间毫秒数
        long start = System.currentTimeMillis();
        gobrsService.gobrsAsync();
        //结束时间: 当前时间 - 开始时间
        long coust = System.currentTimeMillis() - start;
        System.out.println("gobrs-Async " + coust);

    }

    /**
     * Update rule.
     *
     * @param rule the rule
     */
    @RequestMapping("updateRule")
    public void updateRule(@RequestBody Rule rule) {
        gobrsService.updateRule(rule);
    }
}
