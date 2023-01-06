package com.gobrs.async.test.methodtask;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.core.common.domain.TaskResult;
import com.gobrs.async.test.GobrsAsyncTestApplication;
import com.gobrs.async.test.task.methodtask.CaseMethodTaskOne;
import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Case method task.
 *
 * @program: gobrs -async
 * @ClassName CaseTimeout
 * @description:
 * @author: sizegang
 * @create: 2022 -12-09
 */
@SpringBootTest(classes = GobrsAsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseMethodTask {
    @Autowired(required = false)
    private GobrsAsync gobrsAsync;

    @SneakyThrows
    @Test
    public void launcher() {
        Map<String, Object> params = new HashMap<>();

        params.put("normal", Lists.newArrayList(CaseMethodTaskOne.CONTEXT));
        params.put("normal2", Lists.newArrayList("support seize a seat", CaseMethodTaskOne.CONTEXT));

        AsyncResult asyncResult = gobrsAsync.go("launcher", () -> params, 300000);
        final Map<String, TaskResult> resultMap = asyncResult.getResultMap();
        Assertions.assertEquals(resultMap.get("normal").getResult(), CaseMethodTaskOne.TASK_1_RESULT);
    }

    @Test
    void retryTest() {
        gobrsAsync.go("methodRetry", () -> new HashMap<>());
    }

    @Test
    void timoutTest() {
        gobrsAsync.go("methodTimeout", () -> new HashMap<>(), 30);
    }
}
