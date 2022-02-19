package com.jd.gobrs.async.example.controller;

import com.jd.gobrs.async.example.service.GobrsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @program: gobrs-async-example
 * @ClassName GobrsAsyncController
 * @description:
 * @author: sizegang
 * @create: 2022-01-29 20:59
 * @Version 1.0
 **/
@RestController
@RequestMapping("gobrs")
public class GobrsAsyncController {

    @Resource
    private GobrsService gobrsService;


    @RequestMapping("test1")
    public String test1() {
        long start = System.currentTimeMillis();
        gobrsService.testGobrs();
        long end = System.currentTimeMillis() - start;
        System.out.println("总耗时" + end);
        return "success";
    }


    @RequestMapping("test2")
    public String test2() {
        long start = System.currentTimeMillis();
        gobrsService.testGobrs2();
        long end = System.currentTimeMillis() - start;

        System.out.println("总耗时" + end);
        return "success";
    }

    @RequestMapping("test3")
    public String test3() {
        gobrsService.testGobrs3();
        return "success";
    }

}
