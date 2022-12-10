package com.gobrs.async.test.future;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.test.GobrsAsyncTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Future result.
 *
 * @program: gobrs -async
 * @ClassName FutureResult
 * @description:
 * @author: sizegang
 * @create: 2022 -10-31
 */
@SpringBootTest(classes = GobrsAsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FutureResult {

    @Autowired
    private GobrsAsync gobrsAsync;

    /**
     * 获取非强依赖任务的返回结果  如下： C任务获取A任务的返回结果， 从任务配置上看 C并不需要A 执行完成后再执行C 所有 通过Future方式 C有能力获取到A的返回结果
     * Future task rule.
     * - name: "futureTaskRule"
     *   content: "futureTaskA->futureTaskB;futureTaskC->futureTaskD"
     *   task-interrupt: true # 局部异常是否打断主流程 默认 false
     */
    @Test
    public void futureTaskRule() {
        Map<String, Object> params = new HashMap<>();
        gobrsAsync.go("futureTaskRule", () -> params);
    }

}
