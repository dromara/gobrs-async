package com.gobrs.async.example.controller;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.example.service.GobrsService;
import com.gobrs.async.rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: gobrs-async-core
 * @ClassName GobrsController
 * @description:
 * @author: sizegang
 * @create: 2022-03-20
 **/

@RestController
@RequestMapping("gobrs")
public class GobrsController {

    @Autowired
    private GobrsAsync gobrsAsync;

    @Autowired
    private GobrsService gobrsService;

    @RequestMapping("test")
    public String gobrsTest() {
        AsyncResult test = gobrsAsync.go("test", () -> new Object());
        return "success";
    }


    @RequestMapping("future")
    public void future() {
        long start = System.currentTimeMillis();
        gobrsService.future();

        long coust = System.currentTimeMillis() - start;
        System.out.println("future " + coust);

    }

//    @RequestMapping("asyncTool")
//    public void asyncTool() {
//        long start = System.currentTimeMillis();
//        gobrsService.asyncTool();
//        long coust = System.currentTimeMillis() - start;
//        System.out.println("asyncTool " + coust);
//
//    }


    @RequestMapping("gobrsAsync")
    public void setGobrsAsync() {
        long start = System.currentTimeMillis();
        gobrsService.gobrsAsync();
        long coust = System.currentTimeMillis() - start;
        System.out.println("gobrs-Async " + coust);

    }

    @RequestMapping("updateRule")
    public void updateRule(@RequestBody Rule rule) {
        gobrsService.updateRule(rule);
    }
}
