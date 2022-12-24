package com.gobrs.async.test.task.transaction;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.test.GobrsAsyncTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

/**
 * The type Case general.
 *
 * @program: gobrs -async
 * @ClassName CaseOne
 * @description:
 * @author: sizegang
 * @create: 2022 -06-13
 */
@SpringBootTest(classes = GobrsAsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseTransaction {


    @Autowired(required = false)
    private GobrsAsync gobrsAsync;

    /**
     * Tcase.
     */
    @Test
    public void tcase() {

        AsyncResult asyncResult = gobrsAsync.go("transactionRule", () -> new HashMap<>(), 300000);
        System.out.println(asyncResult);
    }

}
