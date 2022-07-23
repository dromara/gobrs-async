package com.gobrs.async.test;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.example.GobrsAsyncExampleApplication;
import com.gobrs.async.example.task.AService;
import com.gobrs.async.example.task.CService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName CaseOne
 * @description:
 * @author: sizegang
 * @create: 2022-06-13
 **/
@SpringBootTest(classes = GobrsAsyncExampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseOne {


    @Autowired
    private GobrsAsync gobrsAsync;

    @Test
    public void tcase() {
        List<String> cases = new ArrayList<>();
        cases.add("CService");
        cases.add("CService");
        cases.add("CService");
        cases.add("CService");
        cases.add("CService");
        cases.add("GService");

        Map<Class, Object> params = new HashMap<>();
        params.put(AService.class, "1");
        params.put(CService.class, "2");

        AsyncResult test = gobrsAsync.go("test", () -> params, cases, 300000);
    }

}
