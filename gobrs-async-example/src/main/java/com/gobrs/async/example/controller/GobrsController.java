package com.gobrs.async.example.controller;

import com.gobrs.async.example.service.GobrsService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private GobrsService gobrsService;


    /**
     * Performance string.
     *
     * @return the string
     */
    @RequestMapping("performance")
    public String performance() {
        gobrsService.performanceTest();
        return Strings.EMPTY;
    }

    /**
     * Gobrs test string.
     *
     * @return the string
     */
    @RequestMapping("testGobrs")
    public String gobrsTest() {
        gobrsService.gobrsTest();
        return "success";
    }


    /**
     * Update rule string.
     *
     * @return the string
     */
    @RequestMapping("updateRule")
    public String updateRule() {
        gobrsService.updateRule();
        return "success";
    }

    /**
     * Future.
     */
    @RequestMapping("future")
    public void future() {
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
        System.out.println("cost " + coust);

    }

    @RequestMapping("optional")
    public void optional() {
        long start = System.currentTimeMillis();
        gobrsService.optionalProcess();
        long coust = System.currentTimeMillis() - start;
        System.out.println("cost " + coust);
    }
}
