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
        gobrsService.testGobrs();
        return "success";
    }
}
