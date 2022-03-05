package com.jd.gobrs.async.example.controller;

import com.jd.gobrs.async.example.service.GobrsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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


    @RequestMapping("testGobrs")
    public String testGobrs(HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        gobrsService.testGobrs(httpServletRequest);
        long end = System.currentTimeMillis() - start;
        System.out.println("总耗时" + end);
        return "success";
    }


    @RequestMapping("testFuture")
    public String testFuture(HttpServletRequest httpServletRequest) {
        long start = System.currentTimeMillis();
        gobrsService.testFuture(httpServletRequest);
        long end = System.currentTimeMillis() - start;

        System.out.println("总耗时" + end);
        return "success";
    }

    @RequestMapping("test")
    public String test3() {
        gobrsService.testGobrsGener();
        return "success";
    }

}
