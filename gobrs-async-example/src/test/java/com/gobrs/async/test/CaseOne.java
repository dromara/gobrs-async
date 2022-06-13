package com.gobrs.async.test;

import com.gobrs.async.GobrsAsync;
import com.gobrs.async.domain.AsyncResult;
import com.gobrs.async.example.GobrsAsyncExampleApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        AsyncResult test = gobrsAsync.go("test", () -> new Object());
    }

}
