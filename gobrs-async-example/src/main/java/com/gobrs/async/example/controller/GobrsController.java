package com.gobrs.async.example.controller;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.domain.AsyncResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("test")
    public String gobrsTest() {
        AsyncResult test = gobrsAsync.go("test", () -> new Object());
        return "success";
    }

}
